(defproject editor "0.2.0-SNAPSHOT"
  :description "A collaborative editor"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.omcljs/om "1.0.0-alpha22"]
                 [figwheel-sidecar "0.4.0" :scope "provided"]
                 [binaryage/devtools "0.4.0"]
                 [cljsjs/quill "0.20.0-0"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]

  :fighwheel {:css-dirs ["resources/public/css"]}

  :cljsbuild {:test-commands {"unit" ["phantomjs"
                                      "phantom/unit-test.js"
                                      "resources/private/html/unit-test.html"]}

              :builds {:dev {:source-paths ["src"]
                             :figwheel true
                             :compiler {:main "editor.core"
                                        :output-to "resources/public/js/main.js"
                                        :output-dir "resources/public/js"
                                        :asset-path "js"
                                        :pretty-print true}}

                       :test {:main "editor.test"
                              :source-paths ["src" "test"]
                              :compiler {:output-to "resources/private/js/unit-test.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}}}

  :clean-targets ^{:protect false} ["resources/public/js"
                                    :target-path])
