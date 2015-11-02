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

(defn edit-line
  "Given a doc, change the content of a line based on the function applied
  to the current value of the line"
  [doc line f]
  (let [line-pid (logoot/index->pid doc line)
        line-content (line-content doc line)]
    (-> (delete doc line-pid)
        (insert-after (dec line) (f line-content)))))

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

;; TODO: split this function o.O
(defn apply-delta
  "Apply Quill delta into a logoot-doc"
  [doc delta]
  (println "delta" delta)
  (loop [dc doc
         ops (:ops delta)
         line 1
         cursor 0]
    (println "looping" dc ops line cursor)
    (cond
      (empty? ops)
      dc

      (= :retain (-> ops first keys first))
      (let [retain (-> ops first :retain)
            doc-line (-> dc vals (nth line))
            doc-line-chars (-> doc-line count inc)]
        (cond
          ;; retain the entire line, but just it
          (= retain doc-line-chars)
          (recur dc (rest ops) (inc line) 0)

          ;; retain the entire line, and more
          (> retain doc-line-chars)
          (recur dc (update-in ops [0 :retain] #(- % doc-line-chars)) (inc line) 0)

          ;; retain part of the line
          (< retain doc-line-chars)
          (recur dc (rest ops) line retain)))

      ;; TODO: make insert and delete work between multiple lines

      (= :insert (-> ops first keys first))
      (let [insert (-> ops first :insert)
            new-doc (edit-line dc line #(insert-at %1 cursor insert))]
        (recur new-doc (rest ops) line (+ cursor (count insert))))

      (= :delete (-> ops first keys first))
      (let [length (-> ops first :delete)
            new-doc (edit-line dc line #(delete-at %1 cursor length))
            new-line (line-content new-doc line)]
        (if-not (= 0 (count new-line))
          (recur new-doc (rest ops) line (+ cursor length))
          (-> (delete new-doc (logoot/index->pid new-doc line))
              (recur (rest ops) line (+ cursor length))))))))

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
