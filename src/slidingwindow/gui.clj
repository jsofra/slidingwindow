(ns slidingwindow.gui
  (:require [seesaw.core :as ss]))

(ss/native!)

(defn start
  "Starts a window and draws an image into it continually.
   image-fn should be a function that can be called to retreive a new image to draw."
  [image-fn w h]
  (let [frm (ss/frame :title "CA" :width w :height h
                      :content (ss/canvas
                                :id :canvas
                                :paint (fn [c g]
                                         (.drawImage g (image-fn) 0 0 nil)
                                         (ss/repaint! c))))]
    (ss/show! frm)))
