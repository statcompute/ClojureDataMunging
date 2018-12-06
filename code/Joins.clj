;; Inner and Outer Joins in Clojure

(require '[clojure.pprint :refer [print-table] :rename {print-table p}]
         '[clojure.set :as s]
         '[clojure.core.reducers :as r])
 
;; CREATE TOY DATASETS                 
(def ds1 [{:id 1 :name "name1"}
          {:id 2 :name "name2"}
          {:id 3 :name "name3"}])
           
(def ds2 [{:id 2 :address "addr2"}
          {:id 3 :address "addr3"}
          {:id 4 :address "addr4"}])
 
;; GET THE HEADER
(def ks ((comp distinct flatten) (map #((comp keys first) %) [ds1 ds2])))
 
;; INNER JOIN WITH SET/JOIN
(p ks
  (s/join ds1 ds2))
 
;| :id | :name | :address |
;|-----+-------+----------|
;|   3 | name3 |    addr3 |
;|   2 | name2 |    addr2 |
 
;; OUTER JOIN #1
(p ks (map #(apply merge %) (vals (group-by :id (concat ds1 ds2)))))
 
;| :id | :name | :address |
;|-----+-------+----------|
;|   1 | name1 |          |
;|   2 | name2 |    addr2 |
;|   3 | name3 |    addr3 |
;|   4 |       |    addr4 |
 
;; OUTER JOIN #2 -- AN EXAMPLE OF USING REDUCERS
(p ks (into () (r/map #(r/reduce merge %) (vals (s/index (s/union ds2 ds1) [:id])))))
 
;| :id | :name | :address |
;|-----+-------+----------|
;|   1 | name1 |          |
;|   4 |       |    addr4 |
;|   3 | name3 |    addr3 |
;|   2 | name2 |    addr2 |
  
 ;; OUTER JOIN #3 -- USE LET CREATING LOCAL VARIABLES
(p ks (let [z1 (zipmap (map :id ds1) ds1) 
            z2 (zipmap (map :id ds2) ds2)]
        (vals (merge-with merge z1 z2))))
 
;| :id | :name | :address |
;|-----+-------+----------|
;|   1 | name1 |          |
;|   2 | name2 |    addr2 |
;|   3 | name3 |    addr3 |
;|   4 |       |    addr4 |
