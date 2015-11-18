(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [editor.quill :as quill]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.string :refer [split-lines join]]))

(enable-console-print!)

;;;; App State ;;;;

(def app-state (let [site (rand-int 1000)
                     clock 0]
                 (atom {:site site
                        :clock 0
                        :doc (-> (logoot/create-doc)
                                 (logoot/insert-after site
                                                      clock
                                                      0
                                                      "Logoot document yo")
                                 (logoot/insert-after site
                                                      clock
                                                      0
                                                      "Logoot document"))})))

;;;; App Helpers ;;;;

(defn create-insert-after
  [site]
  (fn [doc index content]
    (logoot/insert-after doc site (:clock @app-state) index content)))

(def insert-after (create-insert-after (:site @app-state)))
(def delete logoot/delete)

;;;; App Mutation Helpers ;;;;

(defn line-content
  "Given a doc, get the content of a line index"
  [doc line]
  (-> (vals doc)
      (nth line)))

(defn split-lines-with-empty
  "Split lines, but also split empty lines"
  [s]
  (->> (map identity s)
       (map #(if (= %1 "\n") " \n " %1))
       clojure.string/join
       clojure.string/split-lines
       (map clojure.string/trim)
       (#(if (= "" (last %1))
           (butlast %1)
           %1))))

(defn edit-line
  "Given a doc, change the content of a line based on the function applied
  to the current value of the line"
  [doc line f]
  (let [line-pid (logoot/index->pid doc line)
        line-content (line-content doc line)]
    (-> (delete doc line-pid)
        (insert-after (dec line) (f line-content)))))

(defn insert-lines-at
  "Insert lines in a given document in after the given line index"
  [doc index lines]
  (-> (reduce (fn [[doc index] insert]
                [(insert-after doc (dec index) insert)
                 (inc index)])
              [doc
               (inc index)]
              lines)
      first))

(defn insert-at
  "Insert content inside a s[tring] into the cursor position"
  [s cursor content]
  (let [start (subs s 0 cursor)
        end (subs s cursor)]
    (str start content end)))

(defn delete-at
  "Deletes length character from content of s[tring] after cursor"
  [s cursor length]
  (str (subs s 0 cursor) (subs s (+ cursor length))))

;;;; Delta Helpers ;;;;

(defn print-that [_] (println "that"))

(defmulti apply-delta-op (fn [params] (-> params :ops first keys first)))

(defmethod apply-delta-op :default [params] (println "default"))

;; conditions of retain:
;; - retain part of a line
;; - retain entire line
;; - retain entire line and more
;; - retain empty line
(defmethod apply-delta-op :retain
  [{:keys [doc ops line cursor] :as params}]
  (let [retain-chars (-> ops first :retain)
        doc-line (-> doc
                     (line-content line)
                     (subs cursor))
        doc-line-chars (count doc-line)]
    (cond
      ;; nothing to retain
      (= 0 retain-chars)
      (update-in params [:ops] rest)

      ;; retain empty line (count as a char)
      (= 0 doc-line-chars)
      (if (= 0 cursor)
        ;; line not retained yet
        (-> params
            (update-in [:ops 0 :retain] dec)
            (update-in [:cursor] inc))
        ;; line already retained, jump to next
        (-> params
            (update-in [:cursor] (fn [_] 0))
            (update-in [:line] inc)))

      ;; retain exactly the entire line
      (= retain-chars doc-line-chars)
      {:doc doc :ops (rest ops) :line line :cursor doc-line-chars}

      ;; retain the entire line and more
      (> retain-chars doc-line-chars)
      (-> params
          (update-in [:ops 0 :retain] #(- % doc-line-chars))
          (update-in [:line] inc)
          (update-in [:cursor] (fn [_] 0)))

      ;; retain less than the entire line
      (< retain-chars doc-line-chars)
      (-> params
          (update-in [:cursor] #(+ % retain-chars))
          (update-in [:ops] rest)))))


;; conditions of insert:
;; - insert normal chars
;; - insert line-break
;;   - beggining of the line
;;   - middle of the line
;;   - end of the line
(defmethod apply-delta-op :insert
  [{:keys [doc ops line cursor] :as params}]
  (let [insert-content (-> ops first :insert)]
    ;; recursively add each needed line
    (loop [r-params params
           insert-lines (split-lines-with-empty insert-content)]
           (cond
             ;; already inserted all lines
             (empty? insert-lines)
             (update-in r-params [:ops] rest)

             ;; insert normal chars
             (> (-> insert-lines first count) 0)
             (let [r-cursor (:cursor r-params)
                   r-line (:line r-params)
                   r-insert-content (first insert-lines)]
               (recur (-> r-params
                          (update-in [:doc] #(edit-line % r-line (fn [content]
                                                                   (insert-at content r-cursor r-insert-content)))))
                      (rest insert-lines)))

             ;; insert line-break
             (= "" (first insert-lines))
             (let [r-doc (:doc r-params)
                   r-cursor (:cursor r-params)
                   r-line (:line r-params)
                   line-c (line-content r-doc r-line)
                   line-chars (count line-c)]
               (cond
                 ;; at the end of the line
                 (or (= r-cursor line-chars) (= 0 line-chars))
                 (recur (-> r-params
                            (update-in [:doc] #(insert-after % (if (= 0 r-cursor) (inc r-line) r-line) ""))
                            (update-in [:line] inc)
                            (update-in [:cursor] (fn [_] 0)))
                        (rest insert-lines))

                 ;; at the beggining of the line
                 (= 1 r-cursor)
                 (recur (-> r-params
                            (update-in [:doc] #(insert-after % (dec r-line) ""))
                            (update-in [:line] inc))
                        (rest insert-lines))))

             :else
             (update-in r-params [:ops] rest)))))

(defmethod apply-delta-op :delete
  [params]
  (println :delete))

(defn apply-delta
  "Apply Quill delta into a logoot-doc"
  [doc delta]
  (loop [params {:doc doc :ops (:ops delta) :line 1 :cursor 0}]
    (println params)
    (if (empty? (:ops params))
        (:doc params)
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
  (.log js/console source)
  (when (= source "user")
    {:value [:doc]
     :action #(swap! state update-in [:doc] (fn [doc] (apply-delta doc delta)))}))

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
         [:doc])
  Object
  (render [this]
          (let [doc (-> this om/props :doc)]
            (dom/div nil
                     (editor {:content
                              (->> doc
                                   vals
                                   rest
                                   butlast
                                   (join "\n"))
                              :on-text-change
                              #(apply-delta! {:ops (js->clj (aget %1 "ops") :keywordize-keys true)} %2)})
                     (debugger {:doc (-> this om/props :doc)})))))

;;;; Root ;;;;

(om/add-root! reconciler
              App (gdom/getElement "app"))
