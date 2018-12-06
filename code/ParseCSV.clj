;; Parse CSV File with Headers in Clojure

; (defproject prj "0.1.0-SNAPSHOT"
; :dependencies [[org.clojure/clojure "1.8.0"]
; [org.clojars.bmabey/csvlib "0.3.6"]
; [ultra-csv "0.2.1"]
; [incanter "1.5.7"]
; [semantic-csv "0.2.1-alpha1"]
; [org.clojure/data.csv "0.1.4"]])
 
(require '[csvlib :as csvlib]
         '[ultra-csv.core :as ultra]
         '[semantic-csv.core :as semantic]
         '[clojure-csv.core :as csv]
         '[clojure.java.io :as io]
         '[incanter.core :as ic]
         '[incanter.io :as ii])
 
;; INCANTER PACKAGE
(time
(ic/$ (range 0 3) '(:dest :origin :hour)
(ii/read-dataset "/home/liuwensui/Downloads/nycflights.csv" :header true :delim \,)))
 
; "Elapsed time: 14085.382359 msecs"
; | :dest | :origin | :hour |
; |-------+---------+-------|
; | IAH | EWR | 5 |
; | IAH | LGA | 5 |
; | MIA | JFK | 5 |
 
;; CSVLIB PACKAGE
(time
(ic/$ (range 0 3) '(:dest :origin :hour)
(ic/to-dataset
(csvlib/read-csv "/home/liuwensui/Downloads/nycflights.csv" :headers? true))))
 
; "Elapsed time: 0.933955 msecs"
; | dest | origin | hour |
; |------+--------+------|
; | IAH | EWR | 5 |
; | IAH | LGA | 5 |
; | MIA | JFK | 5 |
 
;; ULTRA-CSV PACKAGE
(time
(ic/$ (range 0 3) '(:dest :origin :hour)
(ic/to-dataset
(ultra/read-csv "/home/liuwensui/Downloads/nycflights.csv"))))
 
; "Elapsed time: 30.599593 msecs"
; | :dest | :origin | :hour |
; |-------+---------+-------|
; | IAH | EWR | 5 |
; | IAH | LGA | 5 |
; | MIA | JFK | 5 |
 
;; SEMANTIC-CSV PACKAGE
(time
(ic/$ (range 0 3) '(:dest :origin :hour)
(ic/to-dataset
(with-open [in-file (io/reader "/home/liuwensui/Downloads/nycflights.csv")]
(->> (csv/parse-csv in-file) semantic/mappify doall)))))
 
; "Elapsed time: 8338.366317 msecs"
; | :dest | :origin | :hour |
; |-------+---------+-------|
; | IAH | EWR | 5 |
; | IAH | LGA | 5 |
; | MIA | JFK | 5 |
