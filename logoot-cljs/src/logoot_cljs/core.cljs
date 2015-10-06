(ns logoot-cljs.core
    (:require [clojure.string :as string]))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

;; (enable-console-print!)

;; logoot implementation in cljs

;; insert(pos line)
;; delete(pos)

;; a document is defined by lines
;; line: [pid content]
;; a line identifier is a couple (pos, hs)
;; pos: i(0).i(1)....i(n-1)
;; hs: value of logical clock

;; there are two virtual lines
;; lb and le, to represent the beggining and the end of the document
;; we always need to be able the generate a line A where P < A < N

;; pids are unique
;; pids: (pi, si)
;; pi: integer
;; si: site identifier

;; a position is a list of identifier


;; each site has a logical clock, incremented each time a line is created

(def MAX_INT 32767)

(defn compare-pid
  "Compare two pids. If all intersecting pos identifier are equal, the bigger
  pos vector will win."
  [[pos1] [pos2]]
  ;; 1 if (> (nth 0 pos1) (nth 0 pos2)), -1 if not. if 0, will iterate
  ;; going up until the end of the position vector
  (if (= [pos1] [pos2])
    0
    (if-let [result (first (filter #(not= 0 %)
                                   (map compare pos1 pos2)))]
      result
      ;; if every pos was the same, let the bigger pos vector win
      ;; e.g. (> [[1 2] [3 4]] [[1 2]]) => true
      (if (> (count pos1) (count pos2))
        1
        -1))))

;; (compare-pid [[[2 4]] 5] [[[2 8] [3 5]] 5])

;; a logoot document would be like

(def document
  ;; a hashmap were keys are the pid of the line
  ;; and the values are the content
  (sorted-map-by
   ;; created with this sort fn
   compare-pid
   ;; vector of pid
   ;;|line |site |clock
   [[[0     0]]   nil]
   ;; content
   :lb

   [[[2 1]] 0]
   "This is an example of a Logoot document"

   [[[1 1] [2 2]] 0]
   "This is a line inserted between [1 1] and [2 2]"

   [[[MAX_INT 0]] nil]
   :le))

(defn create-doc
  "Creates an empty logoot document, with its beggining and finish lines"
  []
  (sorted-map-by
   compare-pid
   [[[0 0]] nil]       :lb
   [[[MAX_INT 0]] nil] :le))

(defn zip
  "Creates a list of grouped elements by index"
  [& colls]
  (apply map list colls))

;; (zip [1 2 3] [1 2 3])
;; (zip [[1 2] [6 4]] [[1 6] [9 4]])

(defn rand-int-bet
  "Returns a random integer between x (exclusive) and y (exclusive). Nil
  if there isn't integer between two numbers"
  [x y]
  (cond
    ;; there isn't at least one integer between numbers
    (<= (Math/abs (- x y)) 1)
    nil

    (> x y)
    (+ 1 y (rand-int (- x y 1)))

    (< x y)
    (+ 1 x (rand-int (- y x 1)))))

;; (rand-int-bet 5 7)

(defn rand-pos-bet
  "Generates a random position with lines between two numbers"
  [site l1 l2]
  [(rand-int-bet l1 l2) site])

(defn gen-pos
  "Generate a position between two positions"
  [site pos1 pos2]
  (loop [positions (zip pos1 pos2)
         pos-couple (first positions)
         pos-acc []]
    (let [[p1 p2] pos-couple]
      (cond
        ;; exhausted positions looking for spaces
        (empty? positions)
        (if-not (nil? p1)
          ;; generate position between p1 line and MAX_INT
          (conj pos-acc p1 (rand-pos-bet site (first p1) MAX_INT))
          ;; generate position between pos-acc first line and MAX_INT
          (conj pos-acc (rand-pos-bet site (ffirst pos-acc) MAX_INT)))

        (< (first p1) (first p2))
        (if (> site (second p1))
          ;; generate position between p1 line and p2 line
          (conj pos-acc (rand-pos-bet site (first p1) (first p2)))
          ;; generate position between p1 line and MAX_INT
          (conj pos-acc p1 (rand-pos-bet site (first p1) MAX_INT)))

        :else
        (let [positions (rest positions)
              pos-couple (first positions)
              pos-acc (conj pos-acc p1)]
          (recur positions pos-couple pos-acc))))))

;; (gen-pos 3 [[1 2] [6 4]] [[1 6] [9 4]])
;;   => [[1 2] [6 4] [29728 3]]
;; (gen-pos 7 [[1 2] [6 4]] [[1 6] [9 4]])
;;   => [[1 2] [8 7]]
;; (gen-pos 7 [[1 2]] [[1 6] [9 4]])
;;   => [[1 2] [16998 7]]
;; (gen-pos 3 [[1 2]] [[1 2]])
;;   => [[1 2] [27335 3]]
;; (gen-pos 3 [[1 2]] [[5 2]])
;;   => [[2 3]]


(defn pid->index
  "Returns the index of a given pid"
  [doc pid]
  (first (keep-indexed #(if (= pid %2) %1) (keys doc))))

;; (pid->index document [[[1 1] [2 2]] 0])

(defn index->pid
  "Returns the pid of a given index"
  [doc index]
  (nth (keys doc) index))

;; (index->pid document 1)

(defn insert
  "Inserts the content into pid key of the given document"
  [doc pid content]
  (assoc doc pid content))

;; in this case, should this position be even possible? would it be before or after [[2 1]]?
;; (insert document [[[1 2] [3 4]] 5] "New content")

(defn insert-after
  "Inserts the content after a line index"
  [doc site clock index content]
  (let [[pos1] (index->pid doc index)
        [pos2] (index->pid doc (inc index))]
    (->> (gen-pos site pos1 pos2)
         (#(conj [] % clock))
         (#(insert doc % content)))))

;; (insert-after document 2 20 0 "yo!")

(defn delete
  "Removes pid key from the given document"
  [doc pid]
  (dissoc doc pid))

;; (delete document [[[2 1]] 0])

(defn doc->hash-map
  "Given a logoot document, transform each line to a hash-map"
  [doc]
  (map (fn [[[pos clock] content]]
         {:pos pos :clock clock :content content})
       doc))

(defn pos->logoot-str
  "Given a position, returns a string representation of it"
  [pos]
  (->> pos
       (map (partial string/join ", "))
       (map (fn [x] (str "[" x "]")))
       ((partial string/join "."))
       ))

(defn doc->logoot-str
  "Given a logoot document, returns a string representation of it"
  [doc]
  (->> doc
       ;; get doc info as a hash-map
       (doc->hash-map)

       ;; transform positions to string
       (#(map (fn [line]
                (->> (:pos line)
                     (pos->logoot-str)
                     ((partial assoc line :pos))
                     ))
              %))

       ;; merge position strings and more infos into logoot-str
       (#(map (fn [line]
                  (str "(((" (:pos line) "), " (:clock line) "), " (:content line) ")"))
              %))))

;; (doc->logoot-str document)

;; eventually these should be tests

;; as the current impl is operation-based, but do not replay all operations
;; on every new operation, the document can get into a incosistent state when
;; dissoc operations come before its assoc operation
;; e.g. when we user "a" added a line and deleted it, but user "b" received the
;; delete operation before the addition operation

(= (->> (create-doc)
        (#(insert % [[[1 1]] 0] "First line yo"))
        (#(insert % [[[1 1] [1 3]] 0] "Second line yo (should be deleted)"))
        (#(delete % [[[1 1] [1 3]] 0]))
        (#(insert % [[[1 1] [1 2]] 0] "Second line yo (should exist)")))
   (->> (create-doc)
        (#(delete % [[[1 1] [1 3]] 0]))
        (#(insert % [[[1 1] [1 3]] 0] "Second line yo (should be deleted)"))
        (#(insert % [[[1 1]] 0] "First line yo"))
        (#(insert % [[[1 1] [1 2]] 0] "Second line yo (should exist)"))))

;; one way to solve this kind of issue is to make dissoc and assoc operations
;; write to a history of operations. this can cause a bigger overhead to the
;; document.

;; possible solutions:
;; 1) keep delete operations that can't be fulfilled because the target wasn't
;;    added yet, reapplying it when the target shows up in the document.
;; 2) keep a history of every action on the document, reapplying them each
;;    time a new action comes in. this destroys the non-thombstone advantage of
;;    logoot and can increase the document overhead exponentially.

;; thanks Arnout Engelen (@raboofje) for this amazing talk that shed some light
;; over possible solutions https://goo.gl/IhwWrP
