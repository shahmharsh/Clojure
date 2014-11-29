(ns video-store.core
  (:gen-class)
  (:require [video-store.inventory :as inventory :refer :all]
            [video-store.GUI :as gui :refer :all]))


(defn -main
  [& args]
  (inventory/init)
  (gui/init-frame))
