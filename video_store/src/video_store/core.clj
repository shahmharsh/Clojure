(ns video-store.core
  (:gen-class)
  (:require [video-store.inventory :as inventory :refer :all]
            [video-store.GUI :as gui :refer :all]))


(defn -main
  [& args]
  (inventory/init)
  (inventory/add-movie "ddlj" 2 5)
  (inventory/add-movie "abc" 0.99 7)
  (println (inventory/get-inventory))
  (inventory/add-new-copies "abc" 3)
  (inventory/change-rental-price "abc" 1.49)
  ;(inventory/delete-movie "abc")
  (println (inventory/get-inventory))
  (println (inventory/movie-name 2))
  (println (inventory/price-with-id 1)))
  ;(gui/init))
