(ns video-store.inventory
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [clj-time.local :as local-time]))

; inventory is a vector, where each element in the vector is a map.
; The map contains movie information and is of the format {:id ID, :name movie-name, :rental-price price, :quantity quantity}

; renters is a list of renters, where each element in the vector is a map.
; The map contains renters information and is of the format {:movie-name :renter-name :due-date}

(def inventory (atom []))
(def renters [])

(declare get-inventory)
(declare exists-movie?)
(declare position-in-inventory)
(declare add-new-copies)
(declare delete-movie)
(declare movie-name)
(declare update-key-in-inventory)

(defn- inventory-count
  "Returns zero based count for the inventory."
  []
  (dec (count @inventory)))

(defn- clear-inventory
  "Resets inventory to empty. Used for testing purpose."
  []
  (reset! inventory []))

(defn- get-next-id
  "Returns the next sequential movie id."
  []
  (if (empty? @inventory)
    1
    (let [sorted-inventory (get-inventory)
          last-movie (nth sorted-inventory (inventory-count))]
      (inc (:id last-movie)))))

(defn add-movie
  "Adds movie to inventory. Expects movie-name as string, rental-price (non negative number) and
  quantity (non negative integer) as input. If movie is already present then adds new copies to
  existing inventory. Returns nil."
  [movie-name rental-price quantity]
  {:pre [(string? movie-name) (number? rental-price) (pos? rental-price) (integer? quantity) (pos? quantity)]}
  (if (exists-movie? movie-name)
    (throw (Exception. "MovieAlreadyExistsException"))
    (let [next-id (get-next-id)
          new-movie (hash-map :id next-id, :name movie-name, :rental-price rental-price, :quantity quantity)]
      (swap! inventory #(assoc % (inc (inventory-count)) new-movie)) ;add new movie to next postion in vector
      nil)))

(defn get-inventory
  "Returns entire inventory list sorted by ID. Each element of list is a
  map of format {:id ID, :name movie-name, :rental-price price, :quantity quantity}"
  []
  (sort-by :id @inventory))

(defn- key-not-equal-to-value?
  "Returns true if value is equal to the key value in map movie"
  [key value movie-map]
  (not= value (key movie-map)))

(defn- exists-movie?
  "Returns true if movie exists in inventory."
  [movie-name]
  (not (empty? (drop-while #(key-not-equal-to-value? :name movie-name %) @inventory))))

(defn add-new-copies
  "Adds copies of movie to inventory. Expects movie-name as string and number of copies (non
  negative integer) as input. Throws 'MovieNotFoundException' if movie does not exists. Returns nil."
  [movie-name copies]
  {:pre [(string? movie-name) (pos? copies) (integer? copies)]}
  (update-key-in-inventory movie-name :quantity #(+ copies %)))

(defn- position-in-inventory
  "Returns the index of movie in inventory. Returns nil if movie is not present."
  [movie-name]
  (let [index (count (take-while #(key-not-equal-to-value? :name movie-name %) @inventory))
        total (inventory-count)]
    (when (<= index total)
      index)))

(defn delete-movie
  "Deletes movie from inventory. Expects movie name as string. Returns nil."
  [movie-name]
  {:pre [(string? movie-name)]}
  (when (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)
          start 0
          end (inventory-count)]
      (swap! inventory #(into [] (concat (subvec % start index) (subvec % (+ index 1) (+ end 1)))))))
  nil)

(defn change-rental-price
  "Updates movie with new price. Throws 'MovieNotFoundException' if movie does
  not exists. Returns nil."
  [movie-name price]
  {:pre [(string? movie-name) (pos? price)]}
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (swap! inventory #(assoc-in % [index :rental-price] price))
      nil)
    (throw (Exception. "MovieNotFoundException"))))

(defn- get-value-with-name
  [movie-name key]
  {:pre [(string? movie-name)]}
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (key (get @inventory index)))
    (throw (Exception. "MovieNotFoundException"))))

(defn movie-id
  "Returns movie id. Throws 'MovieNotFoundException' if movie does not exists."
  [movie-name]
  (get-value-with-name movie-name :id))


(defn price-with-name
  "Returns rental-price of movie with movie-name. Throws 'MovieNotFoundException'
  if movie does not exists."
  [movie-name]
  (get-value-with-name movie-name :rental-price))

(defn quantity-with-name
  "Returns quantity of movie with movie-name. Throws 'MovieNotFoundException'
  if movie does not exists."
  [movie-name]
  (get-value-with-name movie-name :quantity))

(defn price-with-id
  "Returns rental-price of movie with id. Throws 'MovieNotFoundException'
  if movie does not exists."
  [id]
  (get-value-with-name (movie-name id) :rental-price))

(defn quantity-with-id
  "Returns quantity of movie with id. Throws 'MovieNotFoundException'
  if movie does not exists."
  [id]
  (get-value-with-name (movie-name id) :quantity))

(defn movie-name
  "Returns movie-name for id. Throws 'MovieNotFoundException' if movie does not exists."
  [id]
  {:pre [(pos? id) (integer? id)]}
  (let [movie (first (drop-while #(key-not-equal-to-value? :id id %) @inventory))]
    (if-not (nil? movie)
      (:name movie)
      (throw (Exception. "MovieNotFoundException")))))

(defn- update-key-in-inventory
  [movie-name key update-function]
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (swap! inventory #(update-in % [index key] update-function))
      nil)
    (throw (Exception. "MovieNotFoundException"))))

(defn- dec-quantity
  "Decrements quantity of movie in inventory by 1. Returns nil."
  [movie-name]
  {:pre [(string? movie-name)]}
  (update-key-in-inventory movie-name :quantity dec)
  nil)

(defn- can-rent?
  "Returns true if number of copies of movie is >= 1."
  [movie-name]
  (not (zero? (quantity-with-name movie-name))))

(comment (defn rent-movie
  ""
  [movie-name renter-name]
  (if (can-rent? movie-name)
    nil)))
