(ns editor.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [editor.logoot :as logoot]
            [reagent.core :as r]))

(enable-console-print!)

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

(defn canvas
  [doc]
  [:textarea {:value (->> doc vals (clojure.string/join "\n"))
              :rows 10}])

(defn on-input
  [e]
  (if (= 13 (.-keyCode e))
    (do (swap! app-state #(assoc %1 :doc
                                 (insert-after (:doc %1) 0 (-> e .-target .-value))))
        (set! (-> e .-target .-value) ""))))

(defn fake-input
  []
  [:input {:on-key-down on-input}])

(defn app
  []
  [:div "Editor do Fabeni"
   (let [doc (:doc @app-state)]
     [:div
      [canvas (-> doc rest butlast)]
      [debugger doc]
      [fake-input]])])

(defn main
  []
  (r/render-component [app]
                      (. js/document (getElementById "app"))))
