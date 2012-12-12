(ns ca.animate
  (:import [javax.swing JPanel JFrame]
           [java.util Timer TimerTask]
           [java.awt Color Graphics2D Toolkit]
           [javax.imageio ImageIO])
  (:require [ca.core :as ca]
            [clojure.java.io :as io]))

(defn panel [image-fn frm]
  (proxy [JPanel] []
    (paint [g]
      (.drawImage g (image-fn) 0 0 nil)
      (.repaint frm))))

(defn start [image-fn w h]
  (let [frm (JFrame. "CA")]
    (doto frm
      (.add (panel image-fn frm))
      (.setSize w h)
      (.setResizable false)
      (.setVisible true))))


(comment
  (defn task [task-fn]
   (proxy [TimerTask] []
     (run [] (task-fn))))

  (defn timer [task delay period]
    (doto (Timer.) (.scheduleAtFixedRate task delay period))))
