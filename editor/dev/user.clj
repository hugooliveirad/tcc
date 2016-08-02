(ns user
  (:require [figwheel-sidecar.repl-api :as ra]))

(defn start+repl []
  (ra/start-figwheel!)
  (ra/cljs-repl))

(defn start []
  (ra/start-figwheel!))

(defn stop [] (ra/stop-figwheel!))

(defn cljs [] (ra/cljs-repl))