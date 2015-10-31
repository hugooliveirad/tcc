(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [editor.quill :as quill]
            [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.string :refer [split-lines]]))

(enable-console-print!)

;;;; App State ;;;;

(def app-state (let [site (rand-int 1000)
                         clock 0]
                     (atom {:site site
                            :clock 0
                            :doc (-> (logoot/create-doc)
                                     (logoot/insert-after site clock 0 "\b"))})))

;;;; Parser ;;;;

(defn read
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(defmulti mutate om/dispatch)

(defmethod mutate 'editor.app/apply-delta
  [{:keys [state]} _ {:keys [delta]}]
  {:value [:doc]
   :action #(swap! state update-in [:doc] (fn [doc] doc))})

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
  (dom/pre nil (-> doc logoot/doc->logoot-str)))

(def editor (om/factory quill/QuillEditor))

(defui App
  static om/IQuery
  (query [this]
         [:doc])
  Object
  (render [this]
          (dom/div nil
                   (editor {:on-text-change #(println % %)})
                   (debugger {:doc (-> this om/props :doc)}))))

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
