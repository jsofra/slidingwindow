(ns ca.matrix
  (:import [cern.colt.function.tdouble Double9Function DoubleFunction]
           [cern.jet.math.tdouble DoubleFunctions]
           [cern.colt.matrix.tdouble.impl DenseDoubleMatrix2D]
           [cern.colt.matrix.tdouble DoubleFactory2D]
           [cern.colt.matrix.tint IntFactory2D]
           [cern.jet.math.tint IntFunctions]
           [ca.parallelcolt.matrix Int9Function MatrixOps]))

(set! *warn-on-reflection* true)

(defn rand-board-d [r c]
  (.assign (.random DoubleFactory2D/dense r c) (DoubleFunctions/round 1.0)))

(defn window-fn-d [f]
  (reify
    Double9Function
    (apply [_ a00 a01 a02 a10 a11 a12 a20 a21 a22]
      (f a00 a01 a02 a10 a11 a12 a20 a21 a22))))

(defn rand-board-i [r c]
  (.assign (.random IntFactory2D/dense r c) (IntFunctions/and 1)))

(defn window-fn-i [f]
  (reify
    Int9Function
    (apply [_ a00 a01 a02 a10 a11 a12 a20 a21 a22]
      (f a00 a01 a02 a10 a11 a12 a20 a21 a22))))

(defn val-converter [mat]
  (condp instance? mat
    cern.colt.matrix.tint.IntMatrix2D int
    cern.colt.matrix.tdouble.DoubleMatrix2D double))

(defn window-fn [mat f]
  (condp instance? mat
    cern.colt.matrix.tint.IntMatrix2D (window-fn-i f)
    cern.colt.matrix.tdouble.DoubleMatrix2D (window-fn-d f)))

(defn set-borders! [mat val]
  (let [val ((val-converter mat) val)
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

(defn split-into-views [mat n]
  (let [r (.rows mat) c (.columns mat)
        step (int (/ r n))
        start-rows (concat (butlast (take (inc n) (iterate #(+ % step) 0))) [(dec r)])]
    (take n
          (map #(.viewPart mat (Math/max (- %1 1) 0) 0 (+ (- %2 %1) 2) c)
               start-rows (rest start-rows)))))

(defn apply-window-fn! [^cern.colt.matrix.tint.IntMatrix2D board
                        ^cern.colt.matrix.tint.IntMatrix2D result
                        ^Int9Function rule-fn]
  (MatrixOps/zAssign8Neighbors board result rule-fn)
  result)

(defn par-apply-window-fn! [^cern.colt.matrix.tint.IntMatrix2D board
                            ^cern.colt.matrix.tint.IntMatrix2D result
                            ^Int9Function rule-fn split-n]
  (let [board-views (split-into-views board split-n)
        result-views (split-into-views result split-n)]
    (doall (pmap #(apply-window-fn! %1 %2 rule-fn) board-views result-views)))
  result)
