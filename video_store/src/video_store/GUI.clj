(ns video-store.GUI
  (:gen-class)
  (:require [video-store.inventory :as inventory :refer :all]
            [seesaw.core :as seesaw]
            [seesaw.swingx :as swingx]))

(declare display-inventory-table)
(declare display-renters-table)

(defn- display
  [content width height]
  (let [window (seesaw/frame :title "Video Store"
                             :content content
                             :width width
                             :height height)]
    (seesaw/show! window)))

(def button-show-movies (seesaw/button :text "Show available movies"
                           :listen [:action (fn [event] (display-inventory-table))]))


(def button-show-renters (seesaw/button :text "Show Renters"
                           :listen [:action (fn [event] (display-renters-table))]))


(def main-panel (seesaw/flow-panel :items [button-show-movies
                                           button-show-renters]))

(def main-tabs (seesaw/tabbed-panel :placement :top
                               :tabs [{:title "A"
                                       :content "This is A's Content"}
                                      {:title "B"
                                       :content "This is B's Content"}]))


(defn- display-renters-table
  []
  (let [renters-list (inventory/get-renters)
        table (swingx/table-x :horizontal-scroll-enabled? true
                              :model [:columns [{:key :renter-name :text "Renter Name"} {:key :movie-name :text "Movie Name"}]
                                      :rows renters-list])]
     (display (seesaw/scrollable table) 300 200)))


(defn- display-inventory-table
  []
  (let [inventory-list (inventory/get-inventory)
        table (swingx/table-x :horizontal-scroll-enabled? true
                              :model [:columns [{:key :name :text "movie-name"} :quantity]
                                      :rows inventory-list])]
     (display (seesaw/scrollable table) 300 200)))



(defn init-frame
  []
  (display main-tabs 300 250))
