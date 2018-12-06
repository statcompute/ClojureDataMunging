;; For Loop and Map in Clojure

;; DEFINE THE DATABASE
(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/home/liuwensui/Downloads/chinook.db"})
 
;; CALL PACKAGES
(require '[clojure.java.jdbc :as j]
         '[criterium.core :as c]
         '[clojure.core.reducers :as r])
 
;; DEFINE THE QUERY TO PULL THE TABLE LIST
(def qry (str "select tbl_name as tbl from sqlite_master where type = 'table';"))
 
;; PULL THE TABLE LIST AND CONVERT IT TO A LIST
(def tbl (apply list (for [i (j/query db qry)] (get i :tbl))))
 
;; PRINT 5 RECORDS OF THE TABLE LIST
(doseq [i (take 5 tbl)] (prn i))
; "albums"
; "sqlite_sequence"
; "artists"
; "customers"
; "employees"
 
;; DEFINE A FUNCTION TO COUNT RECORDS IN A TABLE
(defn cnt [x] (j/query db (str "select '" x "' as tbl, count(*) as cnt from " x ";")))
 
;; TEST THE FUNCTION
(cnt "albums")
; ({:tbl "albums", :cnt 347})
 
;; FOR LOOP
(c/quick-bench (for [i tbl] (cnt i)))
; Execution time mean : 14.156879 ns
 
;; MAP FUNCTION
(c/quick-bench (map cnt tbl))
; Execution time mean : 15.623790 ns
 
;; PMAP FUNCTION - PARALLEL MAP
(c/quick-bench (pmap cnt tbl))
; Execution time mean : 456.780027 µs
 
;; REDUCERS MAP FUNCTION
(defn rmap [f l] (into () (r/map f l)))
(c/quick-bench (rmap cnt tbl))
; Execution time mean : 8.376753 ms
