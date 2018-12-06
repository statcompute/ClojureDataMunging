;; Aggregation by Multiple Keys

(require '[ultra-csv.core :refer [read-csv]]
         '[criterium.core :refer [quick-bench]]
         '[clojure.set :refer [index]])
 
(def ds (read-csv "/home/liuwensui/Downloads/nycflights.csv"))
 
;; FASTEST
(quick-bench
  (map
    (fn [x] {:year (first (key x))
             :month (last (key x))
             :flights (count (val x))})
      (group-by (juxt :year :month) ds)))      
 
;Evaluation count : 6 in 6 samples of 1 calls.
;             Execution time mean : 712.329182 ms
;    Execution time std-deviation : 3.832950 ms
;   Execution time lower quantile : 709.135737 ms ( 2.5%)
;   Execution time upper quantile : 718.651856 ms (97.5%)
;                   Overhead used : 11.694357 ns
 
;; WORKS FINE
(quick-bench
  (map
    (fn [x] {:year (:year (key x))
             :month (:month (key x))
             :flights (count (val x))})
      (group-by #(select-keys % [:year :month]) ds)))
       
;Evaluation count : 6 in 6 samples of 1 calls.
;             Execution time mean : 1.485215 sec
;    Execution time std-deviation : 9.832209 ms
;   Execution time lower quantile : 1.476116 sec ( 2.5%)
;   Execution time upper quantile : 1.500560 sec (97.5%)
;                   Overhead used : 11.694357 ns
 
;; SLOWEST
(quick-bench
  (map
    (fn [x] {:year (:year (key x))
             :month (:month (key x))
             :flights (count (val x))})
      (index ds [:year :month])))
       
;Evaluation count : 6 in 6 samples of 1 calls.
;             Execution time mean : 2.158245 sec
;    Execution time std-deviation : 11.208489 ms
;   Execution time lower quantile : 2.149538 sec ( 2.5%)
;   Execution time upper quantile : 2.175743 sec (97.5%)
;                   Overhead used : 11.694357 ns
