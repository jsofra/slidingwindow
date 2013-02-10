This is just a test of writing some fast sliding window functions using parallel colt from clojure.

It implements that game of life as a sliding window function over an integer matrix.

There is a second implementation that creates a number of views over the matrix, splitting it evenly along the height of the matrix.
The sliding window function is then applied in parallel over the views.

To start a gui from a repl change to the slidingwindow.core namespace and run:

slidingwindow.core> (start-gui (m/rand-board 500 500) game-of-life-rule)

or for the parallel version:

slidingwindow.core> (par-start-gui (m/rand-board 500 500) game-of-life-rule :splits 4)

where splits is the number of jobs to split it into, should be the number of available CPU's.

There is also a couple of bench marks that can be run:

slidingwindow.bench> (bench-gol 1000 1000)

or

slidingwindow.bench> (par-bench-gol 1000 1000 :splits 4)
