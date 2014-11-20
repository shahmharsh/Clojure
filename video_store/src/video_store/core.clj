(ns video-store.core
  (:gen-class)
  (:require [video-store.inventory :as inventory :refer :all]))


(defn -main
  [& args]
  (inventory/add-movie "ddlj" 2 5)
  (inventory/add-movie "abc" 0.99 7)
  (println (inventory/get-inventory))
  (inventory/add-new-copies "abc" 3)
  (inventory/change-movie-rental-price "abc" 1.49)
  (println (inventory/get-inventory))
  (println (inventory/find-movie-name 2))
  (println (inventory/find-price-with-id 1)))
