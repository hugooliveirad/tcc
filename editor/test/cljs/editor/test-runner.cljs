(ns editor.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [editor.core-test]
   [editor.logoot-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'editor.core-test))
    0
    1))
