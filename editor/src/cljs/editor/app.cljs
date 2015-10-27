(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [reagent.core :as r]
            [clojure.string :refer [split-lines]]))

;;;; App State

(defonce app-state (let [site (rand-int 1000)
                         clock 0]
                     (r/atom {:site site
                              :clock 0
                              :doc (-> (logoot/create-doc)
                                       (logoot/insert-after site clock 0 "\b"))})))

(add-watch app-state :watcher
           (fn [key a old-state new-state]
             (println "-- App State Change --")
             (println "app-state" new-state)
             (println "--")))

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

(defn create-insert-after
  [site]
  (fn [doc index content]
    (logoot/insert-after doc site (:clock @app-state) index content)))

;;;; Action functions

(def insert-after (create-insert-after (:site @app-state)))

(def delete logoot/delete)

(defn edit-line
  "Given a doc, change the content of a line based on the function applied
  to the current value of the line"
  [doc line f]
  (let [line-pid (logoot/index->pid doc line)
        line-content (-> (vals doc)
                         (nth line))]
    (-> (delete doc line-pid)
        (insert-after (dec line) (f line-content)))))

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

(defn on-input
  [e]
  (if (= 13 (.-keyCode e))
    (do (swap! app-state #(assoc %1 :doc
                                 (insert-after (:doc %1) (-> (:doc %1) count (- 2)) (-> e .-target .-value))))
        (set! (-> e .-target .-value) ""))))

(defn fake-input
  []
  [:input {:on-key-down on-input}])

(defn app
  []
  [:div "Editor"
   (let [doc (:doc @app-state)]
     [:div
      [canvas (-> doc rest butlast)]
      [debugger doc]
      [fake-input]])])

(defn main
  []
  (r/render-component [app]
                      (. js/document (getElementById "app"))))
