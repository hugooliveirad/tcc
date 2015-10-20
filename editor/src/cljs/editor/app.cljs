(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [reagent.core :as r]))

(defonce app-state (r/atom {:doc (logoot/create-doc)}))
(defonce clock (atom 0))

(defn create-insert-after
  [site]
  (fn [doc index content]
    (do (swap! clock inc)
        (logoot/insert-after doc site @clock index content))))

(defonce insert-after (create-insert-after (rand-int 1000)))

(defn debugger
  [doc]
  [:pre (clojure.string/join "\n" (logoot/doc->logoot-str doc))])

(defn selection-lines
  [dom-node]
  (-> dom-node
      selection/range
      ((partial selection/lines (-> dom-node .-value)))))

(defn on-canvas-change
  [e]
  (let [target (-> e .-target)
        cursor-line (-> (selection-lines target) first inc)
        key-code (-> e .-nativeEvent .-keyCode)
        swap-doc! (fn [f] (swap! app-state #(assoc %1 :doc (f (:doc %1)))))]
    (do (println cursor-line)
        (println (selection-lines target))
        (cond
          ;; new line
          (= 13 key-code)
          (swap-doc! #(insert-after %1 cursor-line "\0"))

          :else
          (println e)))))

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
                :on-key-press on-canvas-change
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
