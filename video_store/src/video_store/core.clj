(ns video-store.core
  (:gen-class)
  (:require [video-store.inventory :as inventory :refer :all]))


(defn -main
  [& args]
  (inventory/add-movie "ddlj" 2 5)
;  (println (inventory/get-inventory))
  (inventory/add-movie "abc" 0.99 7)
  (println (inventory/get-inventory))
  (println (inventory/exists-movie? "abc")))
