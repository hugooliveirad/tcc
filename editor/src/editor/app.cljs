(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [editor.quill :refer [editor]]
            [reagent.core :as r]
            [clojure.string :refer [join]]))

;;;; App State ;;;;

(def app-state& 
  (let [site (rand-int 1000)
        clock 0]
    (r/atom {:site site
             :clock 1
             :cursor 0
             :doc (-> (logoot/create-doc)
                      (logoot/insert-after site
                                           0
                                           0
                                           "a")
                      (logoot/insert-after site
                                           1
                                           1
                                           "b"))})))

;;;; App Helpers ;;;;

(defn create-insert-after
  [site]
  (fn [doc index content clock]
    (logoot/insert-after doc site clock index content)))

(def insert-after (create-insert-after (:site @app-state&)))
(def delete logoot/delete)

;;;; Delta Helpers ;;;;

(defn print-that [_] (println "that"))

(defmulti apply-delta-op (fn [params] 
                           (-> params :ops first keys first)))

(defmethod apply-delta-op :default [params] (.log js/console params) (assoc params :ops []))

(defmethod apply-delta-op :retain
  [{:keys [doc ops cursor] :as params}]
  (.log js/console :retain params)
  (-> params
      (update :cursor + (-> ops first :retain))
      (update :ops (comp vec rest))))

(defmethod apply-delta-op :insert
  [{:keys [doc ops cursor] :as params}]
  (.log js/console :insert params)
  (let [to-insert (-> ops first :insert)
        clock (-> params :clock inc)]
    (if (empty? to-insert)
      (update params :ops rest)
      (-> params
          (assoc :clock clock)
          (update :doc insert-after cursor (first to-insert) clock)
          (update :cursor inc)
          (update :ops vec)
          (update-in [:ops 0 :insert] (comp (partial join "") rest))))))

(defmethod apply-delta-op :delete
  [{:keys [doc ops cursor] :as params}]
  (.log js/console :delete params)
  (let [to-delete (-> ops first :delete)]
    (if (zero? to-delete)
      (update params :ops rest)
      (let [pid (logoot/index->pid doc (inc cursor))]
        (-> params
            (update :doc delete pid)
            (update :ops vec)
            (update-in [:ops 0 :delete] dec))))))

(defn apply-delta
  "Apply Quill delta into a logoot-doc"
  [doc clock delta]
  (loop [params {:doc doc :ops (vec (:ops delta)) :clock clock :cursor 0}]
    (if (empty? (:ops params))
      params
      (recur (apply-delta-op params)))))

(defn apply-event
  "Apply a given event to the state, when source is user"
  [state event source]
  (if-not (= source "user")
    state
    (let [delta {:ops (js->clj (aget event "ops") :keywordize-keys true)}
          new-state (apply-delta (:doc state) (:clock state) delta)]
      (assoc state
             :doc (:doc new-state)
             :clock (:clock new-state)
             :cursor (:cursor new-state)))))

(defn apply-event!
  "Apply a given event to the state atom"
  [state& event source]
  (swap! state& #(apply-event %1 event source)))

;;;; App Components ;;;;

(defn logoot->content
  [doc]
  (->> doc
       :content
       vals
       rest
       butlast
       (join "")
       (#(str % "\n"))))

(defn app
  [props children]
  (let [app-state& (:app-state props)
        {:keys [doc cursor]} @app-state&
        content (logoot->content doc)]
    [:div
     [editor {:cursor cursor
              :content content
              :on-text-change (partial apply-event! app-state&)}]]))

(r/render-component 
  [app {:app-state app-state&}]
  (.getElementById js/document "app"))
