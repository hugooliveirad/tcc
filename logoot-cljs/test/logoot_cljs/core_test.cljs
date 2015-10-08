(ns logoot-cljs.core-test
  (:require [cljs.test :as t]
            [logoot-cljs.core :as sut]))

(def MAX_INT 32767)

(t/deftest testing-compare-pid
  (let [pid-small [[[1 1]] 0]
        pid-small-two-sites [[[1 1] [2 2]] 0]
        pid-medium [[[2 2]] 0]
        pid-medium-two-sites [[[2 2] [3 4]] 0]
        pid-many-sites-medium [[[2 2] [3 4] [5 6] [7 8]] 0]
        pid-big [[[30 5]] 0]
        pid-big-two-sites [[[30 5] [40 7]]]
        pid-big-two-sites-smaller [[[30 5] [35 6]]]]

    (t/testing "equal"
      (t/is (= 0 (sut/compare-pid pid-small pid-small)))
      (t/is (= 0 (sut/compare-pid pid-small-two-sites pid-small-two-sites)))
      (t/is (not= 0 (sut/compare-pid pid-small pid-small-two-sites)))
      (t/is (not= 0 (sut/compare-pid pid-small pid-medium)))
      (t/is (not= 0 (sut/compare-pid pid-big-two-sites pid-small))))

    (t/testing "bigger"
      (t/is (= 1 (sut/compare-pid pid-small-two-sites pid-small)))
      (t/is (= 1 (sut/compare-pid pid-medium pid-small)))
      (t/is (= 1 (sut/compare-pid pid-big pid-medium)))
      (t/is (= 1 (sut/compare-pid pid-big-two-sites pid-big-two-sites-smaller)))
      (t/is (not= 1 (sut/compare-pid pid-small pid-small-two-sites)))
      (t/is (not= 1 (sut/compare-pid pid-small-two-sites pid-big))))

    (t/testing "smaller"
      (t/is (= -1 (sut/compare-pid pid-small pid-big)))
      (t/is (= -1 (sut/compare-pid pid-small pid-small-two-sites)))
      (t/is (= -1 (sut/compare-pid pid-medium pid-big-two-sites)))
      (t/is (= -1 (sut/compare-pid pid-big-two-sites-smaller pid-big-two-sites)))
      (t/is (= -1 (sut/compare-pid pid-many-sites-medium pid-big))))))

(t/deftest testing-create-doc
  (let [doc (sut/create-doc)]

    (t/testing "simple"
      (t/is (= (empty doc) {})))

    (t/testing "content"
      (t/is (= 2 (count doc)))

      (t/is (= [[[0 0]] nil] (ffirst doc)))
      (t/is (= :lb (second (first doc))))

      (t/is (= [[[MAX_INT 0]] nil] (first (second doc))))
      (t/is (= :le (second (second doc)))))

    (t/testing "sorting"
      (t/is (= [[[1 1]] 0] (first (second (sut/insert doc [[[1 1]] 0] "Test"))))))))

(t/deftest testing-zip
  (t/testing "accept any seq"
    (t/is (seq? (sut/zip '(1 2) '(1 2))))
    (t/is (seq? (sut/zip [1 2] [1 2])))
    (t/is (seq? (sut/zip #{1 2} #{1 2})))
    (t/is (seq? (sut/zip {:a 1 :b 2} {:a 1 :b 2})))
    (t/is (seq? (sut/zip (seq '(1 2 3)) (seq '(1 2 3))))))

  (t/testing "same length"
    (t/is (= (seq [[1 2] [3 4]]) (sut/zip [1 3] [2 4])))
    (t/is (= (seq [[3 4] [5 6]]) (sut/zip [3 5] [4 6])))
    (t/is (not= (seq [[1 3] [1 3]]) (sut/zip [1 3] [1 3]))))

  (t/testing "different length"
    (t/is (= (seq [[1 2]]) (sut/zip [1] [2 3])))
    (t/is (= (seq [[1 2]]) (sut/zip [1 3] [2])))
    (t/is (not= (seq [[1]]) (sut/zip [1] [])))
    (t/is (not= (seq [[1 2]]) (sut/zip [1] [2] []))))

  (t/testing "multiple collections"
    (t/is (= (seq [[1 2 3] [4 5 6]]) (sut/zip [1 4] [2 5] [3 6])))
    (t/is (= (seq [[1 2]]) (sut/zip [1 2 3 4 5 6 7] [2])))
    (t/is (not= (seq [[1 4] [1 2]]) (sut/zip [1 4] [1 2]))))

  (t/testing "nil"
    (t/is (not= nil (sut/zip [])))
    (t/is (not= nil (sut/zip [nil] [nil] [nil] nil)))))

(t/deftest testing-rand-int-bet
  (t/testing "simple"
    (let [samples (->> (repeatedly #(sut/rand-int-bet 1 5))
                       (take 40))
          every-sample? #(every? % samples)]
      (t/is (every-sample? #(integer? %)) "integer")
      (t/is (every-sample? #(> % 1)) "greater than first number (exclusive)")
      (t/is (every-sample? #(< % 5)) "less than second number (exclusive)")))

  (t/testing "nothing between"
    (t/is (nil? (sut/rand-int-bet 1 1)) "nil (no integer between)")))

(t/deftest testing-rand-pos-bet
  (t/testing "simple"
    (let [samples (->> (repeatedly #(sut/rand-pos-bet 1 1 5))
                       (take 40)
                       (map first))
          every-sample? #(every? % samples)]
      (t/is (every-sample? #(integer? (first %))) "line a integer")
      (t/is (every-sample? #(integer? (second %))) "site a integer")
      (t/is (every-sample? #(= 1 (second %))) "site as defined")
      (t/is (every-sample? #(= 2 (count %))) "a pos couple")
      (t/is (every-sample? #(> (first %) 1)) "line greater than first line")
      (t/is (every-sample? #(< (second %) 5)) "line less than second line")))

  (t/testing "two positions"
    (let [samples (->> (repeatedly #(sut/rand-pos-bet 1 2 3))
                       (take 40))
          samples-zip (map #(apply sut/zip %) samples)
          samples-lines (mapcat first samples-zip)
          samples-sites (map second samples-zip)]

      (t/is (every? #(> (count %) 1) samples) "a more than one couple position")
      (t/is (every? integer? samples-lines) "a position where every line is an integer"))))

(t/run-tests)