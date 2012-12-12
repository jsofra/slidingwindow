(ns ca.core
  (:require [ca.matrix :as m]
            [ca.gui :as gui]))

(defn game-of-life-rule [a00 a01 a02 a10 a11 a12 a20 a21 a22]
  (let [live (+ a00 a01 a02 a10 a12 a20 a21 a22)]
    (if (or (== live 3) (and (== live 2) (not (zero? a11)))) 1 0)))

(defn board-seq [init-board]
  (iterate reverse [init-board (.copy init-board)]))

(defn start-gui [board rule-fn]
  (let [rule-fn (m/window-fn board rule-fn)
        remaining-boards (atom (board-seq (m/set-borders! board 0)))
        init-image (m/mat-to-image (-> @remaining-boards first first))
        next-img! #(let [[b r] (first (swap! remaining-boards rest))]
                     (m/set-pixels! init-image (m/apply-window-fn! b r rule-fn)))]
    (gui/start next-img! (.columns board) (.rows board))))

(defn par-start-gui [board rule-fn & {:keys [splits] :or {splits 2}}]
  (let [rule-fn (m/window-fn board rule-fn)
        remaining-boards (atom (board-seq (m/set-borders! board 0)))
        init-image (m/mat-to-image (-> @remaining-boards first first))
        next-img! #(let [[b r] (first (swap! remaining-boards rest))]
                     (m/set-pixels! init-image
                                    (m/par-apply-window-fn! b r rule-fn splits)))]
    (gui/start next-img! (.columns board) (.rows board))))
