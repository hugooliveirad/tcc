(defproject editor "0.2.0-SNAPSHOT"
  :description "A collaborative editor"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [binaryage/devtools "0.6.1"]
                 [cljsjs/quill "0.20.0-1"]]

  :plugins [[lein-doo "0.1.6"]
            [lein-figwheel "0.5.3-2"]
            [lein-cljsbuild "1.1.1"]]

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.0-1"]
                                  [com.cemerick/piggieback "0.2.1"]]}
             :user {:plugins [[lein-ancient "0.6.10"]]}}

  :fighwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {:builds {:dev {:source-paths ["src"]
                             :figwheel true
                             :compiler {:main "editor.core"
                                        :output-to "resources/public/js/main.js"
                                        :output-dir "resources/public/js"
                                        :asset-path "js"
                                        :pretty-print true}}}}

  :clean-targets ^{:protect false} ["resources/public/js"
                                    :target-path])
