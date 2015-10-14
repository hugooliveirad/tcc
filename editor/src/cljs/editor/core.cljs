(ns editor.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [editor.logoot :as logoot]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello Chestnut!"}))

(def document (-> (logoot/create-doc)
                  (logoot/insert [[[2 2]] 0] "YO")
                  (logoot/insert [[[1 3]] 1] "I'm on the browser baby")
                  (logoot/insert [(logoot/gen-pos 5 [[1 3]] [[2 2]]) 0] "Between")))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/pre nil (clojure.string/join "\n" (logoot/doc->logoot-str document))))))
    app-state
    {:target (. js/document (getElementById "app"))}))
