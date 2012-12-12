(ns ca.gui-painter
  (:require [seesaw.core :as ss]
            [seesaw.mouse :as ssm]
            [seesaw.color :as ssc]
            [seesaw.graphics :as ssg]))

(ss/native!)

(defn start [image-fn w h]
  (let [img (ssg/buffered-image w h java.awt.image.BufferedImage/TYPE_4BYTE_ABGR)
        bi-g (.createGraphics img)
        frm (ss/frame :title "CA" :width w :height h
                      :content (ss/canvas
                                :id :canvas
                                :paint (fn [c g]
                                         (.drawImage g (image-fn) 0 0 nil)
                                         (.drawImage g img 0 0 nil)
                                         (ss/repaint! c))))]
    (.setColor bi-g (ssc/color :red))
    (ss/listen
     (ss/select frm [:#canvas])
     :mouse-dragged (fn [e]
                      (let [[x y] (map - (ssm/location e) [4 4])]
                        (.fillOval bi-g x y 8 8))))
    (ss/show! frm)))
