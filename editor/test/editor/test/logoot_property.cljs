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
    1 100))

(def gen-counter
  gen/nat)

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
  (gen/vector gen-op 1 50))

(defn apply-op [doc [op pid content]]
  (condp = op
    :insert (e/insert doc pid content)
    :delete (e/delete doc pid)))

(defn apply-ops [doc ops]
  (reduce apply-op doc ops))

(def idempotent
  (prop/for-all 
    [ops gen-ops]
    (is (= (apply-ops doc ops)
           (apply-ops doc ops)))))

(def commutative
  (prop/for-all
    [ops gen-ops]
    (is (= (apply-ops doc ops)
           (apply-ops doc (reverse ops))))))

(defspec idemponent? 10 idempotent)
(defspec commutative? 10 commutative)
