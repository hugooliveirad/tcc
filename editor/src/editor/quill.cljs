(ns editor.quill
  (:require [cljsjs.quill]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Editor
  Object
  (componentDidMount [this]
                     (let [props (om/props this)
                           {:keys [on-text-change content]} props
                           editor (js/Quill. (om/react-ref this :editor))]
                       (when on-text-change
                         (.on editor "text-change" on-text-change))
                       (when content
                         (.setText editor content))
                       (om/set-state! this {:editor editor})))
  ;; FIXME: this one makes it impossible to write special ponctuation,
  ;; as it forces the update of the editor, loosing its typing context.
  ;; maybe we should look for ways to avoid this, by transforming updates
  ;; into deltas, and letting Quill handle when it should update its content
  #_(componentWillReceiveProps [this next]
                             (when (not= (-> this om/props :content)
                                         (-> next :content))
                               (let [editor (om/get-state this :editor)
                                     selection (.getSelection editor)
                                     cursor (-> next :cursor)]
                                 (.setText editor (:content next))
                                 (.setSelection editor cursor cursor))))
  (render [_]
          (dom/div #js {:ref :editor} nil)))

