;; Transpose in Clojure

(require '[huri.core :as h]
         '[clojure.core.matrix.dataset :as d]
         '[incanter.core :as i])
 
;; FROM MAP OF ROWS TO MAP OF COLUMNS
 
(def byRow [{:x 1 :y "a"}
            {:x 2 :y "b"}
            {:x 3 :y "c"}])
 
;; APPROACH #1 - PLAIN CLOJURE
(zipmap (keys (first byRow)) (apply map list (map vals byRow)))
 
; {:x (1 2 3), :y ("a" "b" "c")}
 
;; APPROACH #2 - HURI LIBRARY
(h/col-oriented byRow)
 
; {:x (1 2 3), :y ("a" "b" "c")}
 
;; APPROACH #3 - CORE.MATRIX LIBRARY
(d/to-map (d/dataset (keys (first byRow)) byRow))
 
; {:x [1 2 3], :y ["a" "b" "c"]}
 
;; APPROACH #4 - INCANTER LIBRARY
(i/to-map (i/to-dataset byRow))
 
; {:x (1 2 3), :y ("a" "b" "c")}
 
;; FROM MAP OF COLUMNS TO MAP OF ROWS
 
(def byCol {:x '(1 2 3)
            :y '("a" "b" "c")})
 
;; APPROACH #1 - PLAIN CLOJURE
(map #(zipmap (keys byCol) %) (apply map list (vals byCol)))
 
; ({:x 1, :y "a"} {:x 2, :y "b"} {:x 3, :y "c"})
 
;; APPROACH #2 - HURI LIBRARY
(h/row-oriented byCol)
 
; ({:x 1, :y "a"} {:x 2, :y "b"} {:x 3, :y "c"})
 
;; APPROACH #3 - CORE.MATRIX LIBRARY
(d/row-maps (d/dataset (keys byCol) byCol))
 
; [{:x 1, :y "a"} {:x 2, :y "b"} {:x 3, :y "c"}]
 
;; APPROACH #4 - INCANTER LIBRARY
(second (vals (i/dataset (keys byCol) (apply map list (vals byCol)))))
 
; ({:x 1, :y "a"} {:x 2, :y "b"} {:x 3, :y "c"})
