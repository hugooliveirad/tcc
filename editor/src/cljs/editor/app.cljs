(ns editor.app
  (:require [editor.logoot :as logoot]
            [editor.selection :as selection]
            [reagent.core :as r]
            [clojure.string :refer [split-lines]]))

(defonce app-state (let [site (rand-int 1000)
                         clock 0]
                     (r/atom {:site site
                              :clock 0
                              :doc (-> (logoot/create-doc)
                                       (logoot/insert-after site clock 0 "\0"))})))

(defn create-insert-after
  [site]
  (fn [doc index content]
    (do (swap! app-state #(assoc %1 :clock (inc (:clock %1))))
        (logoot/insert-after doc site (:clock @app-state) index content))))

(def insert-after (create-insert-after (:site @app-state)))
(def delete logoot/delete)

(defn debugger
  [doc]
  [:pre (logoot/doc->logoot-str doc)])

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
    (do (println key-code)
        (cond
          ;; new line
          (= 13 key-code)
          (swap-doc! #(insert-after %1 cursor-line "\0"))

          :else
          (swap-doc! #(let [line-pid (logoot/index->pid %1 cursor-line)
                            line-content (-> (.-value target)
                                             (split-lines)
                                             (nth (dec cursor-line)))
                            input-char (.fromCharCode js/String key-code)]
                        (-> (delete %1 line-pid)
                            (insert-after (dec cursor-line) (str line-content input-char)))))))))

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
