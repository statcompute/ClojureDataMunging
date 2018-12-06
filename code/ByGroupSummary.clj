;; By-Group Statistical Summary in Clojure

(require '[clojure.pprint :as p]
         '[ultra-csv.core :as u]
         '[clj-statistics-fns.core :as s]
         '[clojure.core.reducers :as r])
 
 
(def ds (u/read-csv "/home/liuwensui/Downloads/nycflights.csv"))
 
 
;; SHOW HEADERS OF THE DATA
(prn (keys (first ds)))
 
; (:day :hour :tailnum :arr_time :month :dep_time :carrier :arr_delay :year :dep_delay :origin :flight :distance :air_time :dest :minute)
 
 
;; PRINT A DATA SAMPLE
(p/print-table
  (map #(select-keys % [:origin :dep_delay]) (take 3 ds)))
 
; | :origin | :dep_delay |
; |---------+------------|
; |     EWR |          2 |
; |     LGA |          4 |
; |     JFK |          2 |
 
 
;; APPROACH #1: MAP()
(time
  (p/print-table
    (map
      (fn [x] {:origin (first x)
               :freq (format "%,8d" (count (second x)))
               :nmiss (format "%,8d" (count (filter nil? (map #(get % :dep_delay) (second x)))))
               :med_delay (format "%,8d" (s/median (remove nil? (map #(get % :dep_delay) (second x)))))
               :75q_delay (format "%,8d" (s/kth-percentile 75 (remove nil? (map #(get % :dep_delay) (second x)))))
               :max_delay (format "%,8d" (reduce max (remove nil? (map #(get % :dep_delay) (second x)))))})
        (group-by :origin ds))))
 
; | :origin |    :freq |   :nmiss | :med_delay | :75q_delay | :max_delay |
; |---------+----------+----------+------------+------------+------------|
; |     EWR |  120,835 |    3,239 |         -1 |         15 |      1,126 |
; |     LGA |  104,662 |    3,153 |         -3 |          7 |        911 |
; |     JFK |  111,279 |    1,863 |         -1 |         10 |      1,301 |
; "Elapsed time: 684.71396 msecs"
 
 
;; APPROACH #2: PMAP()
(time
  (p/print-table
    (pmap
      (fn [x] {:origin (first x)
               :freq (format "%,8d" (count (second x)))
               :nmiss (format "%,8d" (count (filter nil? (map #(get % :dep_delay) (second x)))))
               :med_delay (format "%,8d" (s/median (remove nil? (map #(get % :dep_delay) (second x)))))
               :75q_delay (format "%,8d" (s/kth-percentile 75 (remove nil? (map #(get % :dep_delay) (second x)))))
               :max_delay (format "%,8d" (reduce max (remove nil? (map #(get % :dep_delay) (second x)))))})
        (group-by :origin ds))))
 
; | :origin |    :freq |   :nmiss | :med_delay | :75q_delay | :max_delay |
; |---------+----------+----------+------------+------------+------------|
; |     EWR |  120,835 |    3,239 |         -1 |         15 |      1,126 |
; |     LGA |  104,662 |    3,153 |         -3 |          7 |        911 |
; |     JFK |  111,279 |    1,863 |         -1 |         10 |      1,301 |
; "Elapsed time: 487.065551 msecs"
 
 
;; APPROACH #3: REDUCER MAP()
(time
  (p/print-table
    (into ()
      (r/map
        (fn [x] {:origin (first x)
                 :freq (format "%,8d" (count (second x)))
                 :nmiss (format "%,8d" (count (filter nil? (pmap #(get % :dep_delay) (second x)))))
                 :med_delay (format "%,8d" (s/median (remove nil? (pmap #(get % :dep_delay) (second x)))))
                 :75q_delay (format "%,8d" (s/kth-percentile 75 (remove nil? (pmap #(get % :dep_delay) (second x)))))
                 :max_delay (format "%,8d" (reduce max (remove nil? (pmap #(get % :dep_delay) (second x)))))})
          (group-by :origin ds)))))
 
; | :origin |    :freq |   :nmiss | :med_delay | :75q_delay | :max_delay |
; |---------+----------+----------+------------+------------+------------|
; |     JFK |  111,279 |    1,863 |         -1 |         10 |      1,301 |
; |     LGA |  104,662 |    3,153 |         -3 |          7 |        911 |
; |     EWR |  120,835 |    3,239 |         -1 |         15 |      1,126 |
; "Elapsed time: 3734.039994 msecs"
 
 
;; APPROACH #4: LIST COMPREHENSION
(time
  (p/print-table
    (for [g (group-by :origin ds)]
      ((fn [x] {:origin (first x)
                :freq (format "%,8d" (count (second x)))
                :nmiss (format "%,8d" (count (filter nil? (map #(get % :dep_delay) (second x)))))
                :med_delay (format "%,8d" (s/median (remove nil? (map #(get % :dep_delay) (second x)))))
                :75q_delay (format "%,8d" (s/kth-percentile 75 (remove nil? (map #(get % :dep_delay) (second x)))))
                :max_delay (format "%,8d" (reduce max (remove nil? (map #(get % :dep_delay) (second x)))))})
        g))))
 
; | :origin |    :freq |   :nmiss | :med_delay | :75q_delay | :max_delay |
; |---------+----------+----------+------------+------------+------------|
; |     EWR |  120,835 |    3,239 |         -1 |         15 |      1,126 |
; |     LGA |  104,662 |    3,153 |         -3 |          7 |        911 |
; |     JFK |  111,279 |    1,863 |         -1 |         10 |      1,301 |
; "Elapsed time: 692.411023 msecs"
 
 
;; APPROACH #5: LOOP/RECUR
(time
  (p/print-table
    (loop [i (group-by :origin ds) result '()]
      (if ((complement empty?) i)
        (recur
          (rest i)
          (conj result {:origin (first (first i))
                        :freq (format "%,8d" (count (second (first i))))
                        :nmiss (format "%,8d" (count (filter nil? (map #(get % :dep_delay) (second (first i))))))
                        :med_delay (format "%,8d" (s/median (remove nil? (map #(get % :dep_delay) (second (first i))))))
                        :75q_delay (format "%,8d" (s/kth-percentile 75 (remove nil? (map #(get % :dep_delay) (second (first i))))))
                        :max_delay (format "%,8d" (reduce max (remove nil? (map #(get % :dep_delay) (second (first i))))))}))
        result))))
 
; | :origin |    :freq |   :nmiss | :med_delay | :75q_delay | :max_delay |
; |---------+----------+----------+------------+------------+------------|
; |     JFK |  111,279 |    1,863 |         -1 |         10 |      1,301 |
; |     LGA |  104,662 |    3,153 |         -3 |          7 |        911 |
; |     EWR |  120,835 |    3,239 |         -1 |         15 |      1,126 |
; "Elapsed time: 692.717104 msecs"
