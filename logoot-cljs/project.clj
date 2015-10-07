(defproject logoot-cljs "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122" :classifier "aot"
                  :exclusion [org.clojure/data.json]]
                 [org.clojure/data.json "0.2.6" :classifier "aot"]
                 [alembic "0.3.2"]
                 [org.clojure/tools.nrepl "0.2.7"]]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :plugins [[lein-npm "0.6.1"]
            [cider/cider-nrepl "0.10.0-SNAPSHOT"]
            [refactor-nrepl "1.2.0-SNAPSHOT"]
            [lein-cljsbuild "1.1.0"]]
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to "out/dist/logoot_cljs.js"
                                   :output-dir "out"
                                   :verbose true
                                   :pretty-print true}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "out/test/logoot_cljs.js"
                                   :output-dir "out/test"
                                   :verbose true
                                   :pretty-print true}}]}
  :hooks [leiningen.cljsbuild]
  :npm {:dependencies [[source-map-support "0.3.2"]]}
  :source-paths ["src" "target/classes"]
  :clean-targets ["out" "release"]
  :target-path "target")
