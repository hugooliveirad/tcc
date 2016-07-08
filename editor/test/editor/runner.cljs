(ns editor.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [editor.test.logoot]
            [editor.test.logoot-property]))

(doo-tests 'editor.test.logoot
           #_'editor.test.logoot-property)
