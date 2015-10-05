(ns logoot-cljs.test-core
  (:require [logoot-cljs.core :as sut]
            [clojure.test :as t]))

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


(t/run-tests)
