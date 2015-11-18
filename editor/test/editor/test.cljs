(ns editor.test
  (:require  [editor.test.logoot :as logoot]))

(def success 0)

(defn ^:export run []
  (.log js/console "Example test started.")
  (logoot/run)
  success)

