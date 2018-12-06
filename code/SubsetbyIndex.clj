(require '[clojure.pprint :as p]
         '[clojure.java.jdbc :as j])
  
(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/home/liuwensui/Downloads/chinook.db"})
  
(def orders (j/query db "select billingcountry as country, count(*) as orders from invoices group by billingcountry;"))
  
(def clist '("USA" "India" "Canada" "France")
 
(def country (map #(get % :country) orders))
 
(p/print-table 
  (map #(nth orders %) 
    (flatten (pmap (fn [c] (keep-indexed (fn [i v] (if (= v c) i)) country)) clist))))
 
;| :country | :orders |
;|----------+---------|
;|      USA |      91 |
;|    India |      13 |
;|   Canada |      56 |
;|   France |      35 |
