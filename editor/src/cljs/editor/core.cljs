(ns editor.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [editor.logoot :as logoot]))

(enable-console-print!)

(defonce app-state (atom {:doc (logoot/create-doc)}))

(defn insert-bet
  [doc content]
  (let [[pos1] (logoot/index->pid doc 0)
        [pos2] (logoot/index->pid doc 1)]
    (do (println (str "pos1: " pos1 "\npos2: " pos2))
        (logoot/insert doc [(logoot/gen-pos 5 pos1 pos2) 0] content))))

(defn line-input
  [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/input #js {:placeholder "Enter awesome text"
                      :onKeyDown (fn [e]
                                   (let [key (.-keyCode e)
                                         value (.-value (.-target e))]
                                     (if (and (not= value "") (= 13 (.-keyCode e)))
                                       (do
                                         (om/update! app [:doc] (insert-bet (:doc app) (.-value (.-target e))))
                                         (set! (.-value (.-target e)) "")))))}))))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
                   (dom/pre nil (clojure.string/join "\n" (logoot/doc->logoot-str (:doc app))))
                   (om/build line-input app)))))
    app-state
    {:target (. js/document (getElementById "app"))}))
