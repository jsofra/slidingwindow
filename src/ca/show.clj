(ns ca.show
  (:use quil.core)
  (:require [ca.core :as ca])
  (:import [processing.core PImage Color]))

(defn setup []
  (frame-rate 1))

(defn draw []
  (image (PImage. (ca/mat-to-image (ca/rand-board 100 100))) 0 0))

(defsketch ca-sketch
  :title "CA"
  :setup setup
  :draw draw
  :size [100 100])
