;; Subset by Values in Clojure

(require '[clojure.pprint :as p]
         '[clojure.java.jdbc :as j])
 
(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/home/liuwensui/Downloads/chinook.db"})
 
(def orders (j/query db "select billingcountry as country, count(*) as orders from invoices group by billingcountry;"))
 
(def clist '("USA" "India" "Canada" "France"))
 
;; APPROACH #1: LIST COMPREHENSION
(p/print-table 
  (flatten (for [c clist] (for [o orders :when (= c (:country o))] o))))
 
;| :country | :orders |
;|----------+---------|
;|      USA |      91 |
;|    India |      13 |
;|   Canada |      56 |
;|   France |      35 |
 
;; APPROACH #2: FILTER FUNCTION
(p/print-table 
  (flatten (map (fn [c] (filter #(= (:country %) c) orders)) clist)))
 
;| :country | :orders |
;|----------+---------|
;|      USA |      91 |
;|    India |      13 |
;|   Canada |      56 |
;|   France |      35 |
 
;; APPROACH #3: SET/JOIN FUNCTION
(p/print-table 
  (clojure.set/join orders (into () (for [c clist] {:country c}))))
 
;| :country | :orders |
;|----------+---------|
;|      USA |      91 |
;|   France |      35 |
;|    India |      13 |
;|   Canada |      56 |
 
;; APPROACH #4: SET/SELECT FUNCTION
(p/print-table
   (map (fn [c] (into {} (clojure.set/select #(= (:country %) c) (set orders)))) clist))
 
;| :country | :orders |
;|----------+---------|
;|      USA |      91 |
;|    India |      13 |
;|   Canada |      56 |
;|   France |      35 |
 
;; APPROACH #5: REDUCER FUNCTIONS
(require '[clojure.core.reducers :as r])
 
(p/print-table 
  (into () (r/mapcat (fn [c] (r/filter #(= (:country %) c) orders)) clist)))
 
;| :country | :orders |
;|----------+---------|
;|   France |      35 |
;|   Canada |      56 |
;|    India |      13 |
;|      USA |      91 |
