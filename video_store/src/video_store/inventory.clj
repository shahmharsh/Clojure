(ns video-store.inventory
  (:gen-class)
  (:require [me.raynes.fs :as fs]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [clj-time.format :as f]))

; inventory is a vector, where each element in the vector is a map.
; The map contains movie information and is of the format {:id ID, :name movie-name, :rental-price price, :quantity quantity}

; renters is a list of renters, where each element in the vector is a map.
; The map contains renters information and is of the format
; {id: ID, :movie-name movie-name, :renter-name name, :due-date date, :returned false}
; Here :returned is false by default and would se set to true when renter returns the movie

(def ^:private inventory (atom []))
(def ^:private renters (atom []))

(def inventory-file "data-inventory.clj")
(def renters-file "data-renters.clj")

(declare get-inventory)
(declare get-renters)
(declare exists-movie?)
(declare position-in-inventory)
(declare add-new-copies)
(declare delete-movie)
(declare movie-name)
(declare update-key-in-inventory)
(declare quantity-with-name)
(declare change-rental-price)
(declare dec-quantity)
(declare get-movie)

(defn- write-to-file
  [file-name data]
  (with-open [w (clojure.java.io/writer file-name)]
    (binding [*out* w]
      (pr data))))

(defn- read-from-file
  [file-name]
  (if (fs/exists? (str (System/getProperty "user.dir") (System/getProperty "file.separator") file-name))
    (with-open [r (java.io.PushbackReader. (clojure.java.io/reader file-name))]
      (binding [*read-eval* false]
        (read r)))
    (do
      (spit file-name "[]")
      [])))

(defn init
  "Reads from files and initializes internal state."
  []
  (let [inventory-backup (read-from-file inventory-file)
          renters-backup (read-from-file renters-file)]
    (reset! inventory inventory-backup)
    (reset! renters renters-backup)))

(defn- vector-count
  "Returns zero based count for the inventory."
  [vector-atom]
  (dec (count @vector-atom)))

(defn- clear-inventory
  "Resets inventory to empty. Used for testing purpose."
  []
  (reset! inventory []))

(defn- clear-renters
  "Resets renters to empty. Used for testing purpose."
  []
  (reset! renters []))

(defn- get-all-renters
  []
  (into [] (sort-by :id @renters)))

(defn- get-next-id
  "Returns the next sequential id, for movie if parameter is true or for renter if parameter is false."
  [for-movie]
  (if for-movie
    (if (empty? @inventory)
      1
      (let [sorted-inventory (get-inventory)
            last-movie (nth sorted-inventory (vector-count inventory))]
        (inc (:id last-movie))))
    (if (empty? @renters)
      1
      (let [sorted-renters (get-all-renters)
            last-renter (nth sorted-renters (vector-count renters))]
        (inc (:id last-renter))))))

(defn add-movie
  "Adds movie to inventory. Expects movie-name as string, rental-price (non negative number) and
  quantity (non negative integer) as input. If movie is already present then adds copies to movie
  and sets price to passed in price. Returns nil."
  [movie-name rental-price quantity]
  {:pre [(string? movie-name) (number? rental-price) (pos? rental-price) (integer? quantity) (pos? quantity)]}
  (if (exists-movie? movie-name)
    (do
      (change-rental-price movie-name rental-price)
      (add-new-copies movie-name quantity)
      nil)
    (let [next-id (get-next-id true)
          new-movie (hash-map :id next-id, :name movie-name, :rental-price rental-price, :quantity quantity)]
      (swap! inventory #(assoc % (inc (vector-count inventory)) new-movie)) ;add new movie to next postion in vector
      (write-to-file inventory-file @inventory)
      nil)))

(defn get-inventory
  "Returns the available movies in a vector sorted by ID. Each element of vector is a
  map of format {:id ID, :name movie-name, :rental-price price, :quantity quantity}"
  []
  (into [] (sort-by :id @inventory)))

(defn- active-renters
  []
  (filter #(not (:returned %)) @renters))

(defn get-renters
  "Returns a vector of renters who have not returned the movie. Each element of"
  []
  (into [] (sort-by :id (active-renters))))

(defn- key-not-equal-to-value?
  "Returns true if value is equal to the key value in map movie"
  [key value map]
  (not= value (key map)))

(defn- exists-movie?
  "Returns true if movie exists in inventory."
  [movie-name]
  (not (empty? (drop-while #(key-not-equal-to-value? :name movie-name %) @inventory))))

(defn add-new-copies
  "Adds copies of movie to inventory. Expects movie-name as string and number of copies (non
  negative integer) as input. Throws 'MovieNotFoundException' if movie does not exists. Returns nil."
  [movie-name copies]
  {:pre [(string? movie-name) (pos? copies) (integer? copies)]}
  (update-key-in-inventory movie-name :quantity #(+ copies %))
  (write-to-file inventory-file @inventory))

(defn- position-in-inventory
  "Returns the index of movie in inventory. Returns nil if movie is not present."
  [movie-name]
  (let [index (count (take-while #(key-not-equal-to-value? :name movie-name %) @inventory))
        total (vector-count inventory)]
    (when (<= index total)
      index)))

(defn- position-in-renters
  "Returns position of parameter id in renters vector"
  [id]
  (let [index (count (take-while #(key-not-equal-to-value? :id id %) @renters))
        total (vector-count renters)]
    (when (<= index total)
      index)))

(defn- reset-key-in-inventory
  [movie-name key value]
  (if (exists-movie? movie-name)
    (let [index (position-in-inventory movie-name)]
      (swap! inventory #(assoc-in % [index key] value))
      (write-to-file inventory-file @inventory)
      nil)
    (throw (Exception. "MovieNotFoundException"))))

(defn remove-movie
  "Decrements the movie quantity by 1. Expects movie name as string. Returns nil."
  [movie-name]
  {:pre [(string? movie-name)]}
  (let [movie (get-movie movie-name)]
    (when (>= (:quantity movie) 1)
      (do
        (dec-quantity movie-name)
        (write-to-file inventory-file @inventory)))))

(defn change-rental-price
  "Updates movie with new price. Throws 'MovieNotFoundException' if movie does
  not exists. Returns nil."
  [movie-name price]
  {:pre [(string? movie-name) (pos? price)]}
  (reset-key-in-inventory movie-name :rental-price price))

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

(defn- inc-quantity
  "Decrements quantity of movie in inventory by 1. Returns nil."
  [movie-name]
  {:pre [(string? movie-name)]}
  (update-key-in-inventory movie-name :quantity inc)
  nil)

(defn- can-rent?
  "Returns true if number of copies of movie is >= 1."
  [movie-name]
  (not (zero? (quantity-with-name movie-name))))

(def formatter (f/formatter "MM-dd-YYYY"))

(defn- get-due-date
  "Returns due date = current date + 2 weeks formatted as MM-dd-YYYY"
  []
  (f/unparse formatter  (t/plus (l/local-now) (t/weeks 2))))

(defn rent-movie
  "Records renter-name along with movie and decrements from a copy of movie from
  inventory. Throws MovieNotFoundException if movie is not in inventory and NotEnoughCopiesException
  if not enough movie copies available. Returns id of renter record."
  [movie-name renter-name]
  {:pre [(string? movie-name) (string? renter-name)]}
  (if (can-rent? movie-name)
    (let [next-id (get-next-id false)
          new-renter (hash-map :id next-id, :movie-name movie-name, :renter-name renter-name, :due-date (get-due-date) :returned false)]
      (swap! renters #(assoc % (inc (vector-count renters)) new-renter)) ;add new movie to next postion in vector
      (dec-quantity movie-name)
      (write-to-file inventory-file @inventory)
      (write-to-file renters-file @renters)
      next-id)
    (throw (Exception. "NotEnoughCopiesException"))))

(defn return-movie
  "Records that the movie is returned. Throws InvalidIdException if id is not found. Returns nil."
  [id]
  (let [index (position-in-renters id)]
    (if-not (nil? index)
      (do
        (swap! renters #(assoc-in % [index :returned] true))
        (inc-quantity (:movie-name (get @renters index)))
        (write-to-file inventory-file @inventory)
        (write-to-file renters-file @renters))
      (throw (Exception. "InvalidIdException")))))

(defn get-renter-by-id
  "Returns renter map"
  [id]
  (let [renter (filter #(= (:id %) id) @renters)]
    (when-not (empty? renter)
      (first renter))))

(defn get-movie
  "Returns movie map"
  [movie-name]
  (let [movie (first (drop-while #(key-not-equal-to-value? :name movie-name %) @inventory))]
    (if-not (nil? movie)
      movie
      (throw (Exception. "MovieNotFoundException")))))






