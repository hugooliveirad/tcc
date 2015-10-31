(ns editor.quill
  (:require [cljsjs.quill]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Editor
  Object
  (componentDidMount [this]
                     (let [props (om/props this)
                           {:keys [on-text-change]} props
                           editor (js/Quill. "#editor")]
                       (when on-text-change
                         (.on editor "text-change" on-text-change))))
  (render [_]
          (dom/div #js {:id "editor"} nil)))

