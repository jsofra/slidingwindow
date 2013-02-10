(ns slidingwindow.matrix
  (:import [cern.colt.matrix.tint IntFactory2D]
           [cern.jet.math.tint IntFunctions]
           [slidingwindow.parallelcolt.matrix Int9Function MatrixOps]))

(set! *warn-on-reflection* true)

;; convenience functions for working with parallelcolt matrices

(defn rand-board [r c]
  (.assign (.random IntFactory2D/dense r c) (IntFunctions/and 1)))

(defn window-fn
  "Reifies a Int9Function from a regular clojure function."
  [f]
  (reify
    Int9Function
    (apply [_ a00 a01 a02 a10 a11 a12 a20 a21 a22]
      (f a00 a01 a02 a10 a11 a12 a20 a21 a22))))

(defn set-borders! [mat val]
  (let [val (int val)
        rows (.rows mat) cols (.columns mat)]
    (.assign (.viewRow mat 0) val)
    (.assign (.viewRow mat (dec rows)) val)
    (.assign (.viewColumn mat 0) val)
    (.assign (.viewColumn mat (dec cols)) val)
    mat))

(defn set-pixels! [^java.awt.image.BufferedImage img
                   ^cern.colt.matrix.tint.IntMatrix2D mat]
  (let [w (.getWidth img) h (.getHeight img)
        raster (.getRaster img)]
    (.setPixels raster 0 0 w h (.elements mat))
    img))

(defn mat-to-image [^cern.colt.matrix.tint.IntMatrix2D mat]
  (let [w (.columns mat) h (.rows mat)]
    (set-pixels! (java.awt.image.BufferedImage.
                  w h java.awt.image.BufferedImage/TYPE_BYTE_BINARY) mat)))

(defn split-into-views
  "Splits a matrix into n number of views.
   These views can be used to write back into the matrix."
  [^cern.colt.matrix.tint.IntMatrix2D mat n]
  (let [r (.rows mat) c (.columns mat)
        step (int (/ r n))
        start-rows (concat (butlast (take (inc n) (iterate #(+ % step) 0))) [(dec r)])]
    (take n
          (map #(.viewPart mat (Math/max (- %1 1) 0) 0 (+ (- %2 %1) 2) c)
               start-rows (rest start-rows)))))

(defn apply-window-fn!
  "Applies a Int9Function to board writing the new state into result."
  [^cern.colt.matrix.tint.IntMatrix2D board
   ^cern.colt.matrix.tint.IntMatrix2D result
   ^Int9Function rule-fn]
  (MatrixOps/zAssign8Neighbors board result rule-fn)
  result)

(defn par-apply-window-fn!
  "Same as apply-window-fn! but uses pmap to apply the fn to the views in parallel."
  [^cern.colt.matrix.tint.IntMatrix2D result
   ^cern.colt.matrix.tint.IntMatrix2D board-views
   result-views ^Int9Function rule-fn]
  (doall (pmap #(apply-window-fn! %1 %2 rule-fn) board-views result-views))
  result)
