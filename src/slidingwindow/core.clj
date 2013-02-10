(ns slidingwindow.core
  (:require [slidingwindow.matrix :as m]
            [slidingwindow.gui :as gui]))

(defn game-of-life-rule
  "Defines the rules of the game of life for one cell in a 2D neighbourhood.
   The args are the state of the 8 neighbouring cells and the current cell."
  [a00 a01 a02 a10 a11 a12 a20 a21 a22]
  (let [live (+ a00 a01 a02 a10 a12 a20 a21 a22)]
    (if (or (== live 3) (and (== live 2) (not (zero? a11)))) 1 0)))

(defn board-seq
  "A repeating seq of a pair of game state boards, the boards swap each step in the seq.
   The first is the previous state, the second is the current state."
  [init-board]
  (iterate reverse [init-board (.copy init-board)]))

(defn next-board-fn
  "Returns the next board state in the seq having applied the rule fn."
  [remaining-boards rule-fn]
  #(let [[b r] (first (swap! remaining-boards rest))]
     (m/apply-window-fn! b r rule-fn)))

(defn start-gui
  "Starts the gui, creating a function for the gui to call which just provides the next
   board state in the seq as a BufferedImage."
  [board rule-fn]
  (let [remaining-boards (atom (board-seq (m/set-borders! board 0)))
        init-image (m/mat-to-image (-> @remaining-boards first first))
        next-board! (next-board-fn remaining-boards (m/window-fn rule-fn))
        next-img! #(m/set-pixels! init-image (next-board!))]
    (gui/start next-img! (.columns board) (.rows board))))


;; parallel versions

(defn board-view-seq
  "Same as board-seq but also provides matrix views onto the boards.
   The boards will be split into a number of views, the number specified by splits."
  [init-board splits]
  (let [result-board (.copy init-board)
        init-board-views (m/split-into-views init-board splits)
        result-board-views (m/split-into-views result-board splits)]
    (iterate reverse [[init-board init-board-views]
                      [result-board result-board-views]])))

(defn par-next-board-fn
  "Same as next-board but applies the rule fn in parallel."
  [remaining-boards rule-fn]
  #(let [[[b bv] [r rv]] (first (swap! remaining-boards rest))]
     (m/par-apply-window-fn! r bv rv rule-fn)))

(defn par-start-gui
  "Starts the gui, running the rules in parallel.
   The keyword arg :splits defines the number of tasks to split the job into."
  [board rule-fn & {:keys [splits] :or {splits 2}}]
  (let [remaining-boards (atom (board-view-seq (m/set-borders! board 0) splits))
        init-image (m/mat-to-image (-> @remaining-boards first first first))
        next-board! (par-next-board-fn remaining-boards (m/window-fn rule-fn))
        next-img! #(m/set-pixels! init-image (next-board!))]
    (gui/start next-img! (.columns board) (.rows board))))
