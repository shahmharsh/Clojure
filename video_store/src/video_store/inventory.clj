(ns video-store.inventory
  (:gen-class)
  (:require [me.raynes.fs :as fs]))

; inventory is a vector, where each element in the vector is a map.
; The map contains movie information and is of the format {:id ID, :name movie-name, :rental-price price, :quantity quantity}

; renters is a list of renters, where each element in the vector is a map.
; The map contains renters information and is of the format {:movie-name :renter-name :due-date}

(def inventory [])
(def renters [])

(declare get-inventory)
(declare exists-movie?)
(declare position-in-inventory)
(declare add-new-copies)
(declare delete-movie)
(declare find-movie-name)

(defn- get-next-id
  "Returns the next sequential movie id."
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

(comment (defn- key-not-equal-to
  [key movie-name movie]
  (not= movie-name (key movie))))

(defn- exists-movie?
  "Returns true if movie exists in inventory."
  [movie-name]
  (not (empty? (drop-while #(not= movie-name (:name %)) inventory))))

(defn add-new-copies
  "Adds copies of movie to inventory. Throws 'MovieNotFoundException' if movie does
  not exists. Returns nil."
  [movie-name copies]
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (def inventory (update-in inventory [index :quantity] #(+ copies %)))
      nil)
    (throw (Exception. "MovieNotFoundException"))))

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

(defn change-movie-rental-price
  "Updates movie with new price. Throws 'MovieNotFoundException' if movie does
  not exists. Returns nil."
  [movie-name price]
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (def inventory (assoc-in inventory [index :rental-price] price))
      nil)
    (throw (Exception. "MovieNotFoundException"))))

(defn- get-value-with-name
  [movie-name key]
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (key (get inventory index)))
    (throw (Exception. "MovieNotFoundException"))))

(defn find-movie-id
  "Returns movie id. Throws 'MovieNotFoundException' if movie does not exists."
  [movie-name]
  (get-value-with-name movie-name :id))


(defn find-price-with-name
  "Returns rental-price of movie with movie-name. Throws 'MovieNotFoundException'
  if movie does not exists."
  [movie-name]
  (get-value-with-name movie-name :rental-price))

(defn find-quantity-with-name
  "Returns quantity of movie with movie-name. Throws 'MovieNotFoundException'
  if movie does not exists."
  [movie-name]
  (get-value-with-name movie-name :quantity))

(defn find-price-with-id
  "Returns rental-price of movie with id. Throws 'MovieNotFoundException'
  if movie does not exists."
  [id]
  (get-value-with-name (find-movie-name id) :rental-price))

(defn find-quantity-with-id
  "Returns quantity of movie with id. Throws 'MovieNotFoundException'
  if movie does not exists."
  [id]
  (get-value-with-name (find-movie-name id) :quantity))

(defn find-movie-name
  "Returns movie-name for id. Throws 'MovieNotFoundException' if movie does not exists"
  [id]
  (let [movie (first (drop-while #(not= id (:id %)) inventory))]
    (if-not (nil? movie)
      (:name movie)
      (throw (Exception. "MovieNotFoundException")))))

(defn rent-movie
  ""
  [movie-name renter-name]
  )

