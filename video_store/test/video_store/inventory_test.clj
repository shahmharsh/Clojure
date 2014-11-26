(ns video-store.inventory-test
  (:require [clojure.test :refer :all]
            [video-store.inventory :as inventory :refer :all]))

(deftest test-get-next-id
  (testing "Testing get-next-id for inventory"
    (#'inventory/clear-inventory)
    (is (= (#'inventory/get-next-id true) 1))
    (inventory/add-movie "test1" 2 5)
    (is (= (#'inventory/get-next-id true) 2))
    (inventory/add-movie "test2" 2 5)
    (is (= (#'inventory/get-next-id true) 3)))
  (testing "Testing get-next-id for renters"
    (#'inventory/clear-renters)
    (is (= (#'inventory/get-next-id false) 1))
    (inventory/rent-movie "test1" "name1")
    (is (= (#'inventory/get-next-id false) 2))
    (inventory/rent-movie "test2" "name2")
    (is (= (#'inventory/get-next-id false) 3))))

(deftest test-add-movie
  (testing "Testing add-movie"
    (#'inventory/clear-inventory)
    (are [movie-name price quantity] (= (inventory/add-movie movie-name price quantity) nil)
         "test1" 0.99 5
         "test2" 1.99 5
         "test2" 2.99 5))
  (testing "Testing add-movie with illegal arguments."
    (are [movie-name price quantity] (thrown? AssertionError (inventory/add-movie movie-name price quantity))
         123 1 5
         "test3" 1.99 3.3
         "test4" 1.99 -3
         "test5" -1.99 3
         "test6" 0 3
         "test7" 1.99 0)))

(deftest test-get-inventory
  (testing "Testing get-inventory"
    (#'inventory/clear-inventory)
    (is (= (inventory/get-inventory) []))
    (inventory/add-movie "test1" 0.99 3)
    (is (= (inventory/get-inventory) [{:id 1, :name "test1", :rental-price 0.99, :quantity 3}]))
    (inventory/add-movie "test2" 1.49 5)
    (is (= (inventory/get-inventory) [{:id 1, :name "test1", :rental-price 0.99, :quantity 3} {:id 2, :name "test2", :rental-price 1.49, :quantity 5}]))
    (inventory/add-movie "test3" 2.49 6)))

(deftest test-exists-movie?
  (testing "Testing exists-movie?"
    (#'inventory/clear-inventory)
    (is (= (#'inventory/exists-movie? "test") false))
    (inventory/add-movie "test" 0.99 3)
    (is (= (#'inventory/exists-movie? "test") true))))

(deftest test-add-new-copies
  (testing "Testing add-new-copies"
    (#'inventory/clear-inventory)
    (inventory/add-movie "test" 0.99 3)
    (is (= (inventory/quantity-with-id 1) 3))
    (is (= (inventory/add-new-copies "test" 4) nil))
    (is (= (inventory/quantity-with-id 1) 7)))
  (testing "Testing add-new-copies for Exceptions"
    (is (thrown? Exception (inventory/add-new-copies "test1" 4)))
    (are [movie-name quantity] (thrown? AssertionError (inventory/add-new-copies movie-name quantity))
         123 5
         "test" 0
         "test" -3)))

(deftest test-remove-movie
  (testing "Testing remove-movie"
    (#'inventory/clear-inventory)
    (inventory/add-movie "test" 0.99 3)
    (is (= (inventory/remove-movie "test") nil))
    (is (= (inventory/quantity-with-name "test") 0))
    (is (thrown? Exception (inventory/remove-movie "test1")))
    (is (thrown? AssertionError (inventory/remove-movie 123)))))

(deftest test-change-rental-price
  (testing "Testing change-rental-price"
    (#'inventory/clear-inventory)
    (inventory/add-movie "test" 0.99 3)
    (is (= (inventory/change-rental-price "test" 1.99) nil))
    (is (= (inventory/price-with-name "test") 1.99))))

(deftest test-can-rent?
  (testing "Testing can-rent?"
    (#'inventory/clear-inventory)
    (inventory/add-movie "test1" 0.99 3)
    (inventory/add-movie "test2" 0.99 1)
    (#'inventory/dec-quantity "test2")
    (are [movie-name output] (= (#'inventory/can-rent? movie-name) output)
         "test1" true
         "test2" false)))

(deftest test-rent-movie
  (testing "Testing rent-movie"
    (#'inventory/clear-inventory)
    (inventory/add-movie "test1" 0.99 3)
    (inventory/add-movie "test2" 0.99 2)
    (#'inventory/clear-renters)
    (are [movie-name renter-name] (= (inventory/rent-movie movie-name renter-name) nil)
         "test1" "name1"
         "test2" "name2"
         "test2" "name3")
    (is (thrown? Exception (inventory/rent-movie "test2" "name3"))) ;not enough copies of movie available
    (is (thrown? Exception (inventory/rent-movie "test3" "name4")))) ;Movie not found in inventory exception
  (testing "Testing rent-movie with illegal arguments."
    (are [movie-name renter-name] (thrown? AssertionError (inventory/rent-movie movie-name renter-name))
         123 "name5"
         "test2" 123)))




