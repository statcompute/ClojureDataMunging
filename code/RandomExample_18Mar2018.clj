(require '[clojure.pprint :as p]
         '[clojure.java.jdbc :as j])
 
(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/home/liuwensui/Downloads/chinook.db"})
 
(pprint (j/query db "select * from invoices limit 1;"))
 
; ({:invoiceid 1,
;  :customerid 2,
;  :invoicedate "2009-01-01 00:00:00",
;  :billingaddress "Theodor-Heuss-StraÃŸe 34",
;  :billingcity "Stuttgart",
;  :billingstate nil,
;  :billingcountry "Germany",
;  :billingpostalcode "70174",
;  :total 1.98})
 
(def inv (j/query db "select * from invoices;"))
 
;; AGGREGATE INVOICE TOTAL BY COUNTRIES
(def country_sum 
  (map (fn [[billingcountry total]]
    {:billiingcountry billingcountry :total (reduce + (map :total total))})
    (group-by :billingcountry inv)))
 
;; TOP 5 COUNTRIES BY INVOICE AMOUNTS 
(p/print-table (take 5 (reverse (sort-by :total country_sum))))
 
; | :billiingcountry |             :total |
; |------------------+--------------------|
; |              USA |  523.0600000000003 |
; |           Canada |  303.9599999999999 |
; |           France | 195.09999999999994 |
; |           Brazil | 190.09999999999997 |
; |          Germany |             156.48 |
 
;; SELECT ROWS BY CRITERIA, E.G. US ORDERS BETWEEN $10 AND $12
(def us_inv (filter #(and (= (:billingcountry %) "USA") (< 10 (:total %) 12)) inv))
 
;; LIST ORDERS MEETING CRITERIA
(pprint us_inv)
 
;({:invoiceid 298,
;  :customerid 17,
;  :invoicedate "2012-07-31 00:00:00",
;  :billingaddress "1 Microsoft Way",
;  :billingcity "Redmond",
;  :billingstate "WA",
;  :billingcountry "USA",
;  :billingpostalcode "98052-8300",
;  :total 10.91}
; {:invoiceid 311,
;  :customerid 28,
;  :invoicedate "2012-09-28 00:00:00",
;  :billingaddress "302 S 700 E",
;  :billingcity "Salt Lake City",
;  :billingstate "UT",
;  :billingcountry "USA",
;  :billingpostalcode "84102",
;  :total 11.94})
 
;; SELECT COLUMNS, E.G. STATES AND CITIES
(p/print-table (map #(select-keys % [:invoiceid :billingcountry :billingstate :billingcity]) us_inv))
 
; | :invoiceid | :billingcountry | :billingstate |   :billingcity |
; |------------+-----------------+---------------+----------------|
; |        298 |             USA |            WA |        Redmond |
; |        311 |             USA |            UT | Salt Lake City |
