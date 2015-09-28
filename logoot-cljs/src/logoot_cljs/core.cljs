(ns logoot-cljs.core)

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

;; (enable-console-print!)

(println "Hello world!")

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

(compare-pid [[[2 4]] 5] [[[2 8] [3 5]] 5])

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

(defn rand-int-bet
  "Returns a random integer between x (inclusive) and y (exclusive)."
  [x y]
  (if (> x y)
    (+ y (rand-int (- x y)))
    (+ x (rand-int (- y x)))))

;; (rand-int-bet 5 10)

(defn rand-pos-bet
  "Generates a random position with lines between two numbers"
  [site l1 l2]
  [(rand-int-bet l1 l2) site])

(defn gen-pos
  "Generate a position between two positions"
  [site pos1 pos2]
  (loop [positions (zip pos1 pos2)
         pos-couple (first positions)
         p1 (first pos-couple)
         p2 (second pos-couple)
         pos-acc []]
    (cond
      ;; exhausted positions looking for spaces
      ;; generate position between p1 line and MAX_INT
      (empty? positions)
      (conj pos-acc p1 (rand-pos-bet site (first p1) MAX_INT))

      (< (first p1) (first p2))
      (if (> site (second p1))
        ;; generate position between p1 line and p2 line
        (conj pos-acc (rand-pos-bet site (first p1) (first p2)))
        ;; generate position between p1 line and MAX_INT
        (conj pos-acc p1 (rand-pos-bet site (first p1) MAX_INT)))

      :else
      (let [positions (rest positions)
            pos-couple (first positions)
            pos-acc (conj pos-acc p1)
            p1 (first pos-couple)
            p2 (second pos-couple)]
        (recur positions pos-couple p1 p2 pos-acc)))))

(gen-pos 3 [[1 2] [3 4]] [[1 6] [7 8]])

(defn insert
  [doc pid content]
  (assoc doc pid content))

;; in this case, should this position be even possible? would it be before or after [[2 1]]?
(insert document [[[1 2] [3 4]] 5] "New content")

(defn delete
  [doc pid]
  (dissoc doc pid))

(delete document [[[2 1]] 0])
