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
(defmethod apply-delta-op :retain
  [{:keys [doc ops line cursor] :as params}]
  (let [retain-chars (-> ops first :retain)
        doc-line (-> doc
                     (line-content line)
                     (subs cursor))
        doc-line-chars (count doc-line)]
    (cond
      ;; retain exactly the entire line
      (= retain-chars doc-line-chars)
      {:doc doc :ops (rest ops) :line (inc line) :cursor 0}

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


(defmethod apply-delta-op :insert
  [params]
  (println :insert))

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
















(comment



;;;; Helper functions

(defn selection-lines
  "Given a dom-node, returns the selection lines"
  [dom-node]
  (-> dom-node
      selection/sel-range
      ((partial selection/sel-lines (-> dom-node .-value)))))

(defn swap-doc!
  [f]
  (swap! app-state #(assoc-in %1 [:clock] (-> %1 :clock inc)))
  (swap! app-state #(assoc-in %1 [:doc] (-> %1 :doc f))))

(def special-keys {:backspace 8})

(defn special-key?
  "Returns if a key is a special key"
  [key]
  ((complement nil?) ((set (vals special-keys)) key)))


;;;; Action functions



(defn get-key-code
  "Given an event, return the key-code. This normalizes browser behaviors."
  [evt]
  (if (not= 0 (.-keyCode evt))
    (.-keyCode evt)
    (.-charCode evt)))

(defn remove-char
  "Given a direction, cursor-range and a content, return a new string with
  the removal applied"
  [direction cursor-range content]
  (condp = direction
    :backward
    (-> (butlast content)
        (->> (clojure.string/join "")))
    :forward
    content))

;;;; Components (and event handlers)

(defn debugger
  [doc]
  [:pre (logoot/doc->logoot-str doc)])


;; Probably the best way to handle this would be to implement a document editor
;; component, which would export CSP channels for changes. It would likely
;; export events like "line removed", "backspace", "delete", "char inserted"
;; TODO: implement document editor as a more reusable component
(defn on-canvas-key-down
  [e]
  (if (special-key? (get-key-code e))
    (let [target (-> e .-target)
          ;; TODO: handle selections that spans multiple lines
          cursor-range (selection/sel-range target)
          cursor-line (-> (selection-lines target) first inc)
          key-code (get-key-code e)]
      (swap-doc! (cond
                   (= (:backspace special-keys) key-code)
                   ;; TODO: handle multiple line deletions
                   #(edit-line %1 cursor-line (partial remove-char :backward cursor-range)))))))

(defn on-canvas-key-press
  [e]
  (let [target (-> e .-target)
        cursor-line (-> (selection-lines target) first inc)
        key-code (-> e .-nativeEvent get-key-code)]
    (swap-doc!  (cond
                  ;; new line
                  (= 13 key-code)
                  #(insert-after %1 cursor-line "\b")

                  ;; any other key
                  :else
                  #(let [input-char (.fromCharCode js/String key-code)]
                                (edit-line %1 cursor-line (fn [line-content]
                                                            (str line-content input-char))))))))

(def canvas-styles
  {:width "100%"
   :max-width "300px"
   :display "block"})

(defn canvas
  [doc]
  (let [doc-str (->> doc vals (clojure.string/join "\n"))]
    [:textarea {:value doc-str
                :rows 20
                :style canvas-styles
                :on-change #(nil? nil)
                :on-key-down on-canvas-key-down
                :on-key-press on-canvas-key-press
                }]))

(defn app
  []
  [:div "Editor"
   (let [doc (:doc @app-state)]
     [:div
      [canvas (-> doc rest butlast)]
      [debugger doc]])])

(defn main
  []
  (r/render-component [app]
                      (. js/document (getElementById "app"))))
)
