(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'logoot-cljs.core
   :output-to "out/logoot_cljs.js"
   :output-dir "out"})
