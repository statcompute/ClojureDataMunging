;; Clojure Integration with R

(require '[tnoda.rashinban :as rr]
         '[tnoda.rashinban.core :as rc]
         '[clojure.core.matrix.dataset :as dt]
         '[clojure.core.matrix.impl.dataset :as id])
 
;; CREATE A TOY DATA
(def ds [{:id 1.0 :name "name1"}
         {:id 2.0 :name "name2"}
         {:id 3.0 :name "name3"}])
 
;; RUN THE FOLLOWING R CODE IN ADVANCE TO START THE RSERVE SERVER:
;;   R -e 'library(Rserve)' -e 'Rserve(args = "--vanilla")'
;; IF YOU HAVE LITTLER INSTALLED, BELOW ALSO WORKS:
;;   r -e 'library(Rserve); Rserve(args = "--vanilla")'  
(rr/init)
 
;; PASS THE DATA FROM CLOJURE INTO R
(map (fn [x] (rr/<- (name (key x)) (val x))) 
  (let [ks ((comp keys first) ds)] (zipmap ks (map #(map % ds) ks))))
 
(rr/<- 'header (map name ((comp keys first) ds)))
          
;; CREATE THE R DATA.FRAME         
(rc/eval "df = data.frame(lapply(header, as.name))")
 
;; TEST THE R DATA.FRAME
(rc/eval "df$id")
; [1.0 2.0 3.0]
 
(rc/eval "df$name")
; ["name1" "name2" "name3"]
 
;; CONVERT THE R DATA.FRAME BACK TO THE CLOJURE MAP
(def mp (into [] (map #(zipmap (map keyword (rr/colnames 'df)) %) 
                   (partition (count (rr/colnames 'df)) (apply interleave (rr/matrix 'df))))))
 
; [{:id 1.0, :name "name1"} {:id 2.0, :name "name2"} {:id 3.0, :name "name3"}]
 
;; TEST THE EQUALITY BETWEEN INPUT AND OUTPUT DATA
(= mp ds)
; true
 
;; ALTERNATIVELY, WE CAN ALSO CONVERT THE R DATA.FRAME TO A CLOJURE DATASET
(def dt (id/dataset-from-columns (map keyword (rr/colnames 'df)) (rr/matrix 'df)))
 
; #dataset/dataset {:column-names [:id :name], :columns [[1.0 2.0 3.0] ["name1" "name2" "name3"]], :shape [3 2]}
 
;; NEXT, CONVERT THE DATASET TO THE MAP
(def mp2 (dt/row-maps dt))
 
; [{:id 1.0, :name "name1"} {:id 2.0, :name "name2"} {:id 3.0, :name "name3"}]
 
(= ds mp2)
; true
