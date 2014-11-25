(ns video-store.GUI
  (:gen-class)
  (:require [video-store.inventory :as inventory :refer :all]
             [seesaw.core :as seesaw]))

(defn display
  [content width height]
  (let [window (seesaw/frame :title "Video Store"
                             :content content
                             :width width
                             :height height)]
    (seesaw/show! window)))

(def button-show-movies (seesaw/button :text "Show available movies"
                           :listen [:action (fn [event] (seesaw/alert event "Movies"))]))

(def button-show-renters (seesaw/button :text "Show Renters"
                           :listen [:action (fn [event] (display "hi" 300 250))]))


(def main-panel (seesaw/flow-panel :items [button-show-movies
                                             button-show-renters]))



(defn init1
  []
  (display main-panel 300 250))
