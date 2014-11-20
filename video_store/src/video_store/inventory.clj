(ns video-store.inventory
  (:gen-class)
  (:require [me.raynes.fs :as fs]))

; inventory is a list, where each element in the list is a map.
; The map contains movie information and is of the format {:id ID, :name movie-name, :rental-price price, :quantity quantity}

(def inventory [])

(declare get-inventory)
(declare exists-movie?)
(declare position-in-inventory)
(declare add-new-copies)
(declare delete-movie)

(defn- get-next-id
  "Returns the next sequential ID"
  []
  (if (empty? inventory)
    1
    (let [sorted-inventory (get-inventory)
          last-movie (nth sorted-inventory (dec (count inventory)))]
      (inc (:id last-movie)))))

(defn add-movie
  "Adds movie to inventory. If movie is already present then adds new copies to
  existing inventory. Returns id of the movie added or nil if movie already exists."
  [movie-name rental-price quantity]
  (if (exists-movie? movie-name)
    (add-new-copies movie-name quantity)
    (let [next-id (get-next-id)
          new-movie (hash-map :id next-id, :name movie-name, :rental-price rental-price, :quantity quantity)]
      (def inventory (conj inventory new-movie))
      next-id)))

(defn get-inventory
  "Returns entire inventory list sorted by ID. Each element of list is a
  map of format {:id ID, :name movie-name, :rental-price price, :quantity quantity}"
  []
  (sort-by :id inventory))

(defn- exists-movie?
  "Returns true if movie exists in inventory."
  [movie-name]
  (not (empty? (drop-while #(not= movie-name (:name %)) inventory))))

(defn add-new-copies
  "Adds copies of movie to inventory. Returns nil. "
  [movie-name copies]
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (def inventory (update-in inventory [index :quantity] #(+ copies %)))
      nil)
    (throw (Exception. "MovieNotFoundInInventoryException"))))

(defn- position-in-inventory
  "Returns the index of movie in inventory."
  [movie-name]
  (let [index (count (take-while #(not= movie-name (:name %)) inventory))
        total (count inventory)]
    (when (< index total)
      index)))

(defn delete-movie
  "Deletes movie from inventory. Returns nil."
  [movie-name]
  (when (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)
          start 0
          end (count inventory)]
      (def inventory (into [] (concat (subvec inventory start index) (subvec inventory (+ index 1) end))))))
  nil)



