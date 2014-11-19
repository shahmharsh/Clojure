(ns video-store.inventory
  (:gen-class)
  (:require [me.raynes.fs :as fs]))

(def inventory ())
;(def database-file "data.txt")

(declare get-inventory)
(declare exists-movie?)

(defn- get-next-id
  []
  (if (empty? inventory)
    1
    (let [sorted-inventory (get-inventory)
          last-movie (nth sorted-inventory (dec (count inventory)))]
      (inc (:id last-movie)))))

(defn add-movie
  [movie-name rental-price quantity]
  (let [next-id (get-next-id)
        new-movie (hash-map :id next-id, :name movie-name, :rental-price rental-price, :quantity quantity)]
      (def inventory (conj inventory new-movie))))

(defn get-inventory
  []
  (sort-by :id inventory))

(defn exists-movie?
  [movie-name]
  (not (empty? (drop-while #(not= movie-name (:name %)) inventory))))
