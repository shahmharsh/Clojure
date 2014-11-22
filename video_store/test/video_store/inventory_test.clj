(ns video-store.inventory-test
  (:require [clojure.test :refer :all]
            [video-store.inventory :as inventory :refer :all]))

(deftest test-get-next-id
  (testing "Testing get-next-id"
    (#'inventory/clear-inventory)
    (is (= (#'inventory/get-next-id) 1))
    (inventory/add-movie "test1" 2 5)
    (is (= (#'inventory/get-next-id) 2))
    (inventory/add-movie "test2" 2 5)
    (is (= (#'inventory/get-next-id) 3))))

(deftest test-add-movie
  (testing "Testing add-movie"
    (#'inventory/clear-inventory)
    (are [movie-name price quantity] (= (inventory/add-movie movie-name price quantity) nil)
         "test1" 0.99 5
         "test2" 1.99 5))
  (testing "Testing add-movie with illegal arguments."
    (are [movie-name price quantity] (thrown? AssertionError (inventory/add-movie movie-name price quantity))
         123 1 5
         "test3" 1.99 3.3
         "test4" 1.99 -3
         "test5" -1.99 3
         "test6" 0 3
         "test6" 1.99 0)))

(comment (deftest test-get-inventory
  (testing "Testing get-inventory"
    (#'inventory/clear-inventory)
    (is (= inventory/get-inventory '()))
    (inventory/add-movie "test1" 0.99 3)
    (is (= inventory/get-inventory '({:id 1, :name "test1", :rental-price 0.99, :quantity 3})))
    (inventory/add-movie "test2" 1.49 5)
    (is (= inventory/get-inventory '({:id 1, :name "test1", :rental-price 0.99, :quantity 3} {:id 2, :name "test2", :rental-price 1.49, :quantity 5})))
    (inventory/add-movie "test3" 2.49 6))))

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

(deftest test-delete-movie
  (testing "Testing delete-movie"
    (#'inventory/clear-inventory)
    (is (= (inventory/delete-movie "test") nil))
    (inventory/add-movie "test" 0.99 3)
    (is (= (inventory/delete-movie "test") nil))
    (is (thrown? Exception (inventory/movie-id "test"))) ;trying to do any operations on deleted movie will raise an exception
    (is (thrown? AssertionError (inventory/delete-movie 123)))))



