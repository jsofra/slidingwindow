(ns slidingwindow.bench
  (:require [slidingwindow.matrix :as m])
  (:use criterium.core slidingwindow.core))

(defn bench-gol [w h]
  (let [remaining-boards (atom (board-seq (m/set-borders! (m/rand-board w h) 0)))
        next-board! (next-board-fn remaining-boards
                                   (m/window-fn game-of-life-rule))]
    (bench (next-board!))))

(defn par-bench-gol [w h splits]
  (let [remaining-boards (atom (board-view-seq
                                (m/set-borders! (m/rand-board w h) 0) splits))
        next-board! (par-next-board-fn remaining-boards
                                   (m/window-fn game-of-life-rule))]
    (bench (next-board!))))
