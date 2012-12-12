(ns ca.gui
  (:require [seesaw.core :as ss]))

(ss/native!)

(defn start [image-fn w h]
  (let [frm (ss/frame :title "CA" :width w :height h
                      :content (ss/canvas
                                :id :canvas
                                :paint (fn [c g]
                                         (.drawImage g (image-fn) 0 0 nil)
                                         (ss/repaint! c))))]
    (ss/show! frm)))
