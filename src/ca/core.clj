(ns ca.core
  (:require [ca.matrix :as m]
            [ca.gui :as gui])
  (:use criterium.core))

(defn game-of-life-rule [a00 a01 a02 a10 a11 a12 a20 a21 a22]
  (let [live (+ a00 a01 a02 a10 a12 a20 a21 a22)]
    (if (or (== live 3) (and (== live 2) (not (zero? a11)))) 1 0)))

(defn board-seq [init-board]
  (iterate reverse [init-board (.copy init-board)]))

(defn next-board-fn [remaining-boards rule-fn]
  #(let [[b r] (first (swap! remaining-boards rest))]
     (m/apply-window-fn! b r rule-fn)))

(defn par-next-board-fn [remaining-boards rule-fn splits]
  #(let [[b r] (first (swap! remaining-boards rest))]
     (m/par-apply-window-fn! b r rule-fn splits)))

(defn start-gui [board rule-fn]
  (let [remaining-boards (atom (board-seq (m/set-borders! board 0)))
        init-image (m/mat-to-image (-> @remaining-boards first first))
        next-board! (next-board-fn remaining-boards (m/window-fn rule-fn))
        next-img! #(m/set-pixels! init-image (next-board!))]
    (gui/start next-img! (.columns board) (.rows board))))

(defn par-start-gui [board rule-fn & {:keys [splits] :or {splits 2}}]
  (let [remaining-boards (atom (board-seq (m/set-borders! board 0)))
        init-image (m/mat-to-image (-> @remaining-boards first first))
        next-board! (par-next-board-fn remaining-boards (m/window-fn rule-fn) splits)
        next-img! #(m/set-pixels! init-image (next-board!))]
    (gui/start next-img! (.columns board) (.rows board))))

(defn bench-gol [w h]
  (let [remaining-boards (atom (board-seq (m/set-borders! (m/rand-board w h) 0)))
        next-board! (next-board-fn remaining-boards
                                   (m/window-fn game-of-life-rule))]
    (bench (next-board!))))

(defn par-bench-gol [w h splits]
  (let [remaining-boards (atom (board-seq (m/set-borders! (m/rand-board w h) 0)))
        next-board! (par-next-board-fn remaining-boards
                                   (m/window-fn game-of-life-rule) splits)]
    (bench (next-board!))))
