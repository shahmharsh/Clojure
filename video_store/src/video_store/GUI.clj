(ns video-store.GUI
  (:gen-class)
  (:require [video-store.inventory :as inventory]
            [seesaw.core :as seesaw]
            [seesaw.swingx :as swingx]))

(defn- display
  [content width height]
  (let [window (seesaw/frame :title "Video Store"
                             :content content
                             :width width
                             :height height)]
    (-> window
        seesaw/pack!
        seesaw/show!)))

(defn- exit
  [event]
  (let [root (seesaw/to-root event)]
    (seesaw/dispose! root)))

(defn- key-value-in-row
  [key table row]
  (key (seesaw.table/value-at table row)))

(defn- add-new-movie-handler
  [event]
  (let [movie-name (seesaw/input "Enter movie name:")
        price (seesaw/input "Enter movie rental price:")
        copies (seesaw/input "Enter copies:"
                             :value 1)
        root (seesaw/to-root event)
        inventory-table (seesaw/select root [:#inventory-table])]
    (if-not (or (nil? movie-name) (nil? price) (nil? copies))
      (try
        (inventory/add-movie movie-name (Double. price) (Integer. copies))
        (seesaw.table/insert-at! inventory-table 0 (inventory/get-movie movie-name))
        (catch Exception e (seesaw/alert "Invalid Input")))
      (seesaw/alert "Invalid Input"))))

(defn- get-toolbar
  []
  (let [add-movie (seesaw/button :text "Add new movie"
                                 :listen [:action add-new-movie-handler])
        exit-button (seesaw/button :text "Exit"
                                :listen [:action exit])]
  (seesaw/toolbar :floatable? true
                  :orientation :horizontal
                  :items [add-movie
                          :separator
                          exit-button])))


(defn- get-inventory-table
  []
  (seesaw/scrollable (swingx/table-x :id :inventory-table
                                     :horizontal-scroll-enabled? true
                                     :model [:columns [{:key :name :text "Movie Name"}
                                                       {:key :quantity :text "Quantity"}
                                                       {:key :rental-price :text "Price"}]
                                             :rows (inventory/get-inventory)])))

(defn- get-renters-table
  []
  (seesaw/scrollable (swingx/table-x :id :renter-table
                                     :horizontal-scroll-enabled? true
                                     :model [:columns [{:key :renter-name :text "Renter Name"}
                                                       {:key :movie-name :text "Movie Name"}
                                                       {:key :due-date :text "Due Date"}]
                                             :rows (into [] (reverse (inventory/get-renters)))])))

(defn- remove-movie-handler
  [event]
  (let [root (seesaw/to-root event)
        inventory-table (seesaw/select root [:#inventory-table])
        selected-row (seesaw/selection inventory-table)]
    (when-not (nil? selected-row)
      (let [movie-name (key-value-in-row :name inventory-table selected-row)]
        (inventory/remove-movie movie-name)
        (seesaw.table/update-at! inventory-table selected-row (inventory/get-movie movie-name))))))

(defn- add-copies-handler
  [event]
  (let [copies (seesaw/input "Enter number of copies:"
                             :value 1)
        root (seesaw/to-root event)
        inventory-table (seesaw/select root [:#inventory-table])
        selected-row (seesaw/selection inventory-table)]
    (when-not (or (nil? copies) (nil? selected-row))
      (try
        (let [movie-name (key-value-in-row :name inventory-table selected-row)]
          (inventory/add-new-copies movie-name (Integer. copies))
          (seesaw.table/update-at! inventory-table selected-row (inventory/get-movie movie-name)))
        (catch Exception e (seesaw/alert "Invalid Copies"))))))

(defn- rent-movie-handler
  [event]
  (let [root (seesaw/to-root event)
        inventory-table (seesaw/select root [:#inventory-table])
        renter-table (seesaw/select root [:#renter-table])
        selected-row (seesaw/selection inventory-table)]
    (if (nil? selected-row)
      (seesaw/alert "Select movie from table")
      (let [renter-name (seesaw/input "Enter Renter Name")
            movie-name (key-value-in-row :name inventory-table selected-row)
            renter-id (inventory/rent-movie movie-name renter-name)
            renter-record (inventory/get-renter-by-id renter-id)]
        (when-not (nil? renter-record)
          (do
            (seesaw.table/insert-at! renter-table 0 renter-record)
            (seesaw.table/update-at! inventory-table selected-row (inventory/get-movie movie-name))))))))

(defn- get-inventory-buttons
  []
  (seesaw/grid-panel :columns 1
                    :items [(seesaw/button :text "Rent Movie"
                                           :listen [:action rent-movie-handler])
                            (seesaw/button :text "Add Copies"
                                           :listen [:action add-copies-handler])
                            (seesaw/button :text "Remove Movie"
                                           :listen [:action remove-movie-handler])]))
(defn- find-movie-row
  [movie-name]
  (let [inventory (inventory/get-inventory)]
    (count (take-while #(not= movie-name (:name %)) inventory))))

(defn- return-movie-handler
  [event]
  (let [root (seesaw/to-root event)
        renter-table (seesaw/select root [:#renter-table])
        inventory-table (seesaw/select root [:#inventory-table])
        container (seesaw/select root [:#movie-actions])
        selected-row (seesaw/selection renter-table)]
    (when-not (nil? selected-row)
      (do
        (let [id (key-value-in-row :id renter-table selected-row)
              movie-name (key-value-in-row :movie-name renter-table selected-row)]
          (inventory/return-movie id)
          (seesaw.table/remove-at! renter-table selected-row)
          (seesaw.table/update-at! inventory-table (find-movie-row movie-name) (inventory/get-movie movie-name)))))))

(defn- get-renter-buttons
  []
  (seesaw/button :text "Return Movie"
                 :size [40 :by 40]
                 :listen [:action return-movie-handler]))

(defn- get-inventory-tab
  []
  (seesaw/border-panel :id :movie-actions
                       :north (get-toolbar)
                       :center (seesaw/left-right-split (get-inventory-table)
                                                        (get-inventory-buttons))))

(defn- get-renters-tab
  []

  (seesaw/border-panel :north (seesaw/toolbar :floatable? true
                                              :orientation :horizontal
                                              :items [(seesaw/button :text "Exit"
                                                                     :listen [:action exit])])
                       :center (seesaw/left-right-split (get-renters-table)
                                                        (get-renter-buttons))))

(defn- get-main-frame
  []
  (seesaw/tabbed-panel :placement :top
                       :tabs [{:title "Movies"
                               :content (get-inventory-tab)}
                              {:title "Renters"
                               :content (get-renters-tab)}]))


(defn init-frame
  []
  (display (get-main-frame) 300 400))


