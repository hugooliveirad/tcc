(ns editor.logoot
    (:require [clojure.string :as string]))

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
  {:cemetery
   ;; a hashmap that stores visibility degrees for keys in :content
   {}

   :content
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

     [[[1 1]] 0] "a"

     [[[2 1]] 0] "b" 

     [[[3 1]] 0] "c" 

     [[[MAX_INT 0]] nil]
     :le)
   })

(defn create-doc
  "Creates an empty logoot document, with its beggining and finish lines"
  []
  {:cemetery {}
   :content (sorted-map-by
              compare-pid
              [[[0 0]] nil]       :lb
              [[[MAX_INT 0]] nil] :le)})

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
  (if-let [rand-line (rand-int-bet l1 l2)]
    [[rand-line site]]
    [[l1 site] [(rand-int-bet 0 MAX_INT) site]]))

;; (rand-pos-bet 5 1 5)
;; (rand-pos-bet 5 1 2)
;; (rand-pos-bet 5 (dec MAX_INT) MAX_INT)

(defn gen-pos
  "Generate a position between two positions"
  [site pos1 pos2]
  (loop [p1 (first pos1)
         p2 (first pos2)
         pos1-rest pos1
         pos2-rest pos2
         pos-acc []]
    (cond
      ;; exhausted positions looking for spaces
      (or (empty? pos1-rest) (empty? pos2-rest))
      (if-not (nil? p1)
        ;; generate position between p1 line and MAX_INT
        (into pos-acc (concat [p1] (rand-pos-bet site (first p1) MAX_INT)))
        (if (empty? pos2-rest)
          ;; generate position between pos-acc first line and MAX_INT
          (into pos-acc (rand-pos-bet site (ffirst pos-acc) MAX_INT))
          ;; generate position between pos-acc first line and last line of pos2-rest
          (into pos-acc (concat (butlast pos2-rest) (rand-pos-bet site (ffirst pos-acc) (first (last pos2-rest)))))))

      (< (first p1) (first p2))
      (if (>= site (second p1))
        ;; generate position between p1 line and p2 line
        (if (= (count pos1-rest) (count pos2-rest))
          (into pos-acc (rand-pos-bet site (first p1) (first p2)))
          (into pos-acc (concat (butlast pos1-rest) (rand-pos-bet site (first (last pos1-rest)) MAX_INT))))
        ;; generate position between p1 line and MAX_INT
        (if (= (count pos1-rest) (count pos2-rest))
          (into pos-acc (concat [p1] (rand-pos-bet site (first p1) MAX_INT)))
          (into pos-acc (concat (butlast pos1-rest) (rand-pos-bet site (first (last pos1-rest)) MAX_INT)))))

      :else
      (let [pos-acc (conj pos-acc p1)
            pos1-rest (rest pos1-rest)
            pos2-rest (rest pos2-rest)
            p1 (first pos1-rest)
            p2 (first pos2-rest)]
        (recur p1 p2 pos1-rest pos2-rest pos-acc)))))

;; (gen-pos 3 [[1 2] [6 4]] [[1 6] [9 4]]) ;; => [[1 2] [6 4] [29728 3]]
;; (gen-pos 7 [[1 2] [6 4]] [[1 6] [9 4]]) ;; => [[1 2] [8 7]]
;; (gen-pos 7 [[1 2]] [[1 6] [9 4]]) ;; => [[1 2] [16998 7]]
;; (gen-pos 3 [[1 2]] [[1 2]]) ;; => [[1 2] [27335 3]]
;; (gen-pos 3 [[1 2]] [[5 2]]) ;; => [[2 3]]

(defn doc-keys
  "Returns all the keys from the :content"
  [doc]
  (-> doc :content keys))

(defn pid->index
  "Returns the index of a given pid"
  [doc pid]
  (first (keep-indexed #(if (= pid %2) %1) (doc-keys doc))))

;; (pid->index document [[[1 1] [2 2]] 0])

(defn index->pid
  "Returns the pid of a given index"
  [doc index]
  (nth (doc-keys doc) index nil))

;; (index->pid document 1)

(defn degree
  "Returns the visibility degree of a pid. 0 if not in cemetery."
  [doc pid]
  (if-let [d (get-in doc [:cemetery pid])]
    d
    0))

(defn assoc-degree
  "Assoc a visibility degree to a pid"
  [doc pid degree]
  (if (= 0 degree)
    (update doc :cemetery dissoc pid)
    (assoc-in doc [:cemetery pid] degree)))

(defn insert
  "Inserts the content into pid key of the given document"
  [doc pid content]
  (let [d (inc (degree doc pid))]
    (if (= d 1)
      (assoc-in doc [:content pid] content)
      (assoc-degree doc pid d))))

;; in this case, should this position be even possible? would it be before or after [[2 1]]?
;; (insert document [[[1 2] [3 4]] 5] "New content")

(defn insert-after
  "Inserts the content after a line index"
  [doc site clock index content]
  (let [[pos1] (index->pid doc index)
        [pos2] (index->pid doc (inc index))]
    (as-> 
      (gen-pos site pos1 pos2) $
      (conj [] $ clock)
      (insert doc $ content))))

;; (insert-after document 2 20 0 "yo!")

(defn delete
  "Removes pid key from the given document"
  [doc pid]
  (if (pid->index doc pid)
    (-> doc
        (update :content dissoc pid)
        (assoc-degree pid 0))
    (assoc-degree doc pid (dec (degree doc pid)))))

;; (delete document [[[2 1]] 0])

(defn doc->hash-map
  "Given a logoot document, transform each line to a hash-map"
  [doc]
  (map (fn [[[pos clock] content]]
         {:pos pos :clock clock :content content})
       (:content doc)))

(defn pos->logoot-str
  "Given a position, returns a string representation of it"
  [pos]
  (->> pos
       (map (partial string/join ", "))
       (map (fn [x] (str "[" x "]")))
       (string/join ".")))

(defn doc->logoot-str
  "Given a logoot document, returns a string representation of it"
  [doc]
  (->> doc
       ;; get doc info as a hash-map
       (doc->hash-map)

       ;; transform positions to string
       (map (fn [line]
                (->> (:pos line)
                     (pos->logoot-str)
                     (assoc line :pos))))

       ;; merge position strings and more infos into logoot-str
       (map (fn [line]
              (str "(((" (:pos line) "), " (:clock line) "), " (:content line) ")")))
       (clojure.string/join "\n")))

;; (doc->logoot-str document)
