;; Updating Column Values in Clojure Map

(require '[huri.core :as h]
         '[clojure.core.matrix.dataset :as d]
         '[incanter.core :as i])
 
(def ds [{:id 1.0 :name "name1"}
         {:id 2.0 :name "name2"}
         {:id 3.0 :name "name3"}])
 
;; UPDATE THE :NAME COLUMN IN THE DATASET
;; - IF THE VALUE IS NOT "NAME2", THEN CHANGE TO "NOT 2"
;;
;; EXPECTED OUTPUT:
;; | :id | :name |
;; |-----+-------|
;; | 1.0 | not 2 |
;; | 2.0 | name2 |
;; | 3.0 | not 2 |
 
;; WITH CLOJURE.CORE/UPDATE
(def d1 (map (fn [x] (update x :name #(if (= "name2" %) % "not 2"))) ds))
 
;; WITH CLOJURE.CORE/UPDATE-IN
(def d2 (map (fn [x] (update-in x [:name] #(if (= "name2" %) % "not 2"))) ds))
 
;; WITH HURI/UPDATE-COLS
(def d3 (h/update-cols {:name #(if (= "name2" %) % "not 2")} ds))
 
;; WITH MATRIX.DATASET/EMAP-COLUMN
(def d4 (-> ds
            (d/dataset)
            (d/emap-column :name #(if (= "name2" %) % "not 2"))
            ((comp #(map into %) d/row-maps))))
    
;; WITH INCANTER/TRANSFORM-COL
(def d5 (-> ds
            (i/to-dataset)
            (i/transform-col :name #(if (= "name2" %) % "not 2"))
            ((comp #(map into %) second vals))))
