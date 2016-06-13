(ns editor.core
  (:require #_[editor.app :as app]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)
(devtools/set-pref! :install-sanity-hints true)
