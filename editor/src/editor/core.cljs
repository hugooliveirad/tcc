(ns editor.core
  (:require [editor.app :as app]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)
(devtools/set-pref! :install-sanity-hints true)

(println "Hello yo")
(println {:thats "crazy"})

(def crazy {:a 1 :b 2 :d { :nest "that" :yeah :yo }})
(println crazy)

#_(defn main
  []
  (app/main))
