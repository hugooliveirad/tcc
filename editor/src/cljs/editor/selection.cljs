(ns editor.selection
  (:require [clojure.string :refer [split-lines]]))

(defn range
  "Returns the selection range inside a dom-node"
  [dom-node]
  [(-> dom-node .-selectionStart)
   (-> dom-node .-selectionEnd)])

(defn lines
  "Given a string and a selection-range, returns a seq of the lines this
  selection spans. If the string is empty, [-1] will be returned"
  [string [range-start range-end]]
  (if (= 0 (count string))
    [-1]
    (->> (split-lines string)
         ;; count line sizes
         (map count)
         ;; get a tuple with the end and start sum of chars for each line
         (reduce (fn [sums size]
                   (let [last-sum (if-let [last (-> sums last last)]
                                    last
                                    -1)]
                     (conj sums [(count sums)
                                 (+ last-sum 1)
                                 (+ last-sum 1 size)]))) [])
         ;; get the index of the lines the selection ranges
         (reduce (fn [lines [index start-sum end-sum]]
                   (if (and (>= end-sum range-start) (<= start-sum range-end))
                     (conj lines index)
                     lines)) []))))
