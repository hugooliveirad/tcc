(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [editor.quill :as quill]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.string :refer [join]]))

;;;; App State ;;;;

(def app-state (let [site (rand-int 1000)
                     clock 0]
                 (atom {:site site
                        :clock 2
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
  (fn [doc index content]
    (logoot/insert-after doc site (:clock @app-state) index content)))

(def insert-after (create-insert-after (:site @app-state)))
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
  (let [to-insert (-> ops first :insert)]
    (if (empty? to-insert)
      (update params :ops rest)
      (-> params
          (update :doc insert-after cursor (first to-insert))
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
  [doc delta]
  (loop [params {:doc doc :ops (vec (:ops delta)) :cursor 0}]
    (if (empty? (:ops params))
      params
      (recur (apply-delta-op params)))))

;;;; Parser ;;;;

(defn read
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(defmulti mutate om/dispatch)

(defmethod mutate 'editor.app/apply-delta
  [{:keys [state]} _ {:keys [delta source]}]
  (when (= source "user")
    {:value {:keys [:doc]}
     :action #(swap! state 
                     (fn [state]
                       (let [new-state (apply-delta (:doc state) delta)]
                         (assoc state 
                                :doc (:doc new-state)
                                :cursor (:cursor new-state)))))}))

(def app-parser (om/parser {:read read :mutate mutate}))

;;;; Reconciler ;;;;

(def reconciler
  (om/reconciler
   {:state app-state
    :parser app-parser}))

;;;; App Transactions ;;;;

(defn apply-delta!
  "Apply a given delta to the state document"
  [delta source]
  (om/transact! reconciler `[(apply-delta ~{:delta delta :source source})]))

;;;; App Components ;;;;

(defn debugger
  [{:keys [doc] :as props}]
  (dom/pre nil (->> doc logoot/doc->logoot-str)))

(def editor (om/factory quill/Editor))

(defui App
  static om/IQuery
  (query [this]
         [:doc :cursor])
  Object
  (render [this]
          (let [doc (-> this om/props :doc)
                cursor (-> this om/props :cursor)]
            (.log js/console "app cursor" cursor)
            (dom/div nil
                     (editor {:cursor cursor
                              :content
                              (->> doc
                                   :content
                                   vals
                                   rest
                                   butlast
                                   (join "")
                                   (#(str % "\n")))
                              :on-text-change
                              #(apply-delta! {:ops (js->clj (aget %1 "ops") :keywordize-keys true)} %2)})
                     (debugger {:doc (-> this om/props :doc)})
                     (dom/pre nil (:cemetery (-> this om/props :doc)))))))

;;;; Root ;;;;

(om/add-root! reconciler
              App (gdom/getElement "app"))
