;; Adding New Columns to Clojure Map

(require '[huri.core :as h]
         '[clojure.core.matrix.dataset :as d]
         '[incanter.core :as i])
 
(def ds [{:id 1.0 :name "name1"}
         {:id 2.0 :name "name2"}
         {:id 3.0 :name "name3"}])
 
;; ADD 2 COLUMNS TO THE DATASET
;; - ADD 2 TO ID AND NAME ADD2
;; - CHECK NAME = "name2" AND NAME NAME2
;;
;; EXPECTED OUTPUT:
;;| :id | :name | :add2 | :name2 |
;;|-----+-------+-------+--------|
;;| 1.0 | name1 |   3.0 |      N |
;;| 2.0 | name2 |   4.0 |      Y |
;;| 3.0 | name3 |   5.0 |      N |
 
;; WITH PLAIN CLOJURE
;; #1 - MERGE
(def d1 (map #(merge % {:add2 (+ (:id %) 2) 
                        :name2 (if (= "name2" (:name %)) "Y" "N")}) ds))
 
;; #2 - MERGE-WITH
(def d2 (map #(merge-with into % {:add2 (+ (:id %) 2)
                                  :name2 (if (= "name2" (:name %)) "Y" "N")}) ds))
 
;; #3 - ASSOC
(def d3 (map #(assoc % :add2 (+ (:id %) 2) 
                       :name2 (if (= "name2" (:name %)) "Y" "N")) ds))
 
;; #4 - CONJ
(def d4 (map #(conj % {:add2 (+ (:id %) 2)
                       :name2 (if (= "name2" (:name %)) "Y" "N")}) ds))
 
;; #5 - CONCAT 
(def d5 (map #(into {} (concat % {:add2 (+ (:id %) 2)
                                  :name2 (if (= "name2" (:name %)) "Y" "N")})) ds))
 
;; WITH HURI 
(def d6 (h/derive-cols {:name2 [#(if (= "name2" %) "Y" "N") :name]
                        :add2 [#(+ 2  %) :id]} ds))
 
;; WITH CORE.MATRIX API
(def d7 (-> ds
            (d/dataset)
            (d/add-column :add2 (map #(+ 2 %) (map :id ds)))
            (d/add-column :name2 (map #(if (= "name2" %) "Y" "N") (map :name ds)))
            (d/row-maps)))
 
;; WITH INCANTER API
(def d8 (->> ds
             (i/to-dataset)
             (i/add-derived-column :add2 [:id] #(+ 2 %))
             (i/add-derived-column :name2 [:name] #(if (= "name2" %) "Y" "N"))
             ((comp second vals))))
 
;; CHECK THE DATA EQUALITY
(= d1 d2 d3 d4 d5 d6 d7 d8)
;; true
