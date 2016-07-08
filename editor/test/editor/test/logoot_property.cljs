(ns editor.test.logoot-property
  (:require [cljs.test :refer-macros [is]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [editor.logoot :as e]))

(def doc (e/create-doc))

(def gen-pos
  (gen/vector
    (gen/vector gen/nat 2)
    1 10))

(def gen-counter
  (gen/fmap str gen/uuid))

(def gen-pid
  (gen/tuple
    gen-pos
    gen-counter))

(def gen-op
  (gen/tuple
    (gen/elements [:insert :delete])
    gen-pid
    gen/string))

(def gen-ops
  (gen/vector gen-op 1 10))

(defn apply-op [doc [op pid content]]
  (condp = op
    :insert (e/insert doc pid content)
    :delete (e/delete doc pid)))

(defn apply-ops [ops doc]
  (reduce apply-op doc ops))

(def idempotent
  (prop/for-all 
    [ops gen-ops]
    (is (= (:content (apply-ops ops doc))
           (:content (apply-ops ops doc))))))

(def commutative
  (prop/for-all
    [ops gen-ops]
    (is (= (:content (apply-ops ops doc))
           (:content (apply-ops (reverse ops) doc))))))

(def associative
  (prop/for-all 
    [ops1 gen-ops
     ops2 gen-ops
     ops3 gen-ops]
    (let [apply1 (partial apply-ops ops1)
          apply2 (partial apply-ops ops2)
          apply3 (partial apply-ops ops3)]
      (is (= (:content (apply1 (apply2 (apply3 doc))))
             (:content (apply2 (apply1 (apply3 doc))))
             (:content (apply3 (apply2 (apply1 doc)))))))))

(defspec idempotent?  100 idempotent)
(defspec commutative? 100 commutative)
(defspec associative? 100 associative)

