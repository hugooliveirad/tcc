(ns editor.quill
  (:require [cljsjs.quill]
            [reagent.core :as r]))

(defn editor
  [props children]
  (r/create-class
    {:component-did-mount
     (fn [this]
       (let [props (r/props this)
             {:keys [on-text-change content]} props
             editor (js/Quill. (.getElementById js/document "editor"))]
         (when on-text-change
           (.on editor "text-change" on-text-change))
         (when content
           (.setText editor content))))
     :reagent-render
     (fn [props children]
       [:div#editor])}))

