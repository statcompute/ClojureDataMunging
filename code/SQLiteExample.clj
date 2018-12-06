;; Clojure and SQLite

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/home/liuwensui/Downloads/chinook.db"})

;; project.clj
;; (defproject prj "0.1.0-SNAPSHOT"
;;   :dependencies [[org.clojure/clojure "1.8.0"]
;;                  [org.clojure/java.jdbc "0.7.5"]
;;                  [org.xerial/sqlite-jdbc "3.7.2"]
;; ])
 
(require '[clojure.pprint :as p]
         '[clojure.java.jdbc :as j])
 
(p/print-table (j/query db (str "select tbl_name, type from sqlite_master where type = 'table' limit 3;")))
 
;; |       :tbl_name | :type |
;; |-----------------+-------|
;; |          albums | table |
;; | sqlite_sequence | table |
;; |         artists | table |

;; project.clj
;; (defproject prj "0.1.0-SNAPSHOT"
;;   :dependencies [[org.clojure/clojure "1.8.0"]
;;                  [clojureql "1.0.5"]
;;                  [org.xerial/sqlite-jdbc "3.7.2"]
;; ])
 
(require '[clojure.pprint :as p]
         '[clojureql.core :as l])
 
(p/print-table @(-> (l/select (l/table db :sqlite_master) (l/where (= :type "table")))
                    (l/take 3)
                    (l/project [:tbl_name :type])))
 
;; | :type |       :tbl_name |
;; |-------+-----------------|
;; | table |          albums |
;; | table | sqlite_sequence |
;; | table |         artists |
