(ns assignment4.core-test
  (:require [clojure.test :refer :all]
            [clojure.zip :as zip]
            [assignment4.min-heap :as min-heap :refer :all]))

(def numbers-heap [4 [10 [20 nil nil] nil] [5 [13 nil nil] nil]])
(def string-heap ["aing" ["bing" ["c" nil nil] nil] ["ding" nil nil]])

(deftest test-preorder-traversal
  (testing "Testing preorder-traversal for min-heap")
  (are [heap result] (= (min-heap/preorder-traversal heap) result)
       numbers-heap '(4 10 20 5 13)
       string-heap '("aing" "bing" "c" "ding")
       [1 nil nil] '(1)
       ["abc" nil nil] '("abc")
       [] []))

(deftest test-min-heap-insert-numbers
  (testing "Testing insert for numbers")
  (are [heap x result] (= (min-heap/insert heap x) result)
       numbers-heap 21 [4 [10 [20 nil nil] [21 nil nil]] [5 [13 nil nil] nil]]
       numbers-heap 9 [4 [9 [20 nil nil] [10 nil nil]] [5 [13 nil nil] nil]]
       numbers-heap 1 [1 [4 [20 nil nil] [10 nil nil]] [5 [13 nil nil] nil]]
       [] 5 [5 nil nil]))

(deftest test-min-heap-insert-strings
  (testing "Testing insert for strings")
  (are [heap x result] (= (min-heap/insert heap x) result)
       string-heap "e" ["aing" ["bing" ["c" nil nil] nil] ["ding" ["e" nil nil] nil]]
       string-heap "abc" ["abc" ["bing" ["c" nil nil] nil] ["aing" ["ding" nil nil] nil]]
       [] "abc" ["abc" nil nil]))

(deftest test-ends-in-ing?
  (testing "Testing ends-in-ing function")
  (are [input result] (= (#'min-heap/ends-in-ing? input) result)
       "abc" false
       "coding" true
       "" false
       "ing" true))

(deftest test-node-height
  (testing "Testing node-height function")
  (are [input height] (= (#'min-heap/node-height input) height)
       (zip/vector-zip numbers-heap) 3
       (zip/vector-zip [4 [10 nil nil] nil]) 2
       (zip/vector-zip [4 nil nil]) 1))

(deftest test-swap-value
  (testing "Testing swap-value function")
  (are [node value result] (= (zip/node (#'min-heap/swap-value node value)) result)
       (zip/vector-zip [4 nil nil]) 5 [5 nil nil]
       (zip/vector-zip ["abc" nil nil]) "pqr" ["pqr" nil nil]))

(deftest test-left-child
  (testing "Testing zipper->left-child function")
  (are [node result] (= (zip/node (#'min-heap/zipper->left-child node)) result)
       (zip/vector-zip [4 nil nil]) nil
       (zip/vector-zip [4 [5 nil nil] [6 nil nil]]) [5 nil nil]
       (zip/vector-zip [4 [5 [8 [9 nil nil] nil] nil] [6 nil nil]]) [5 [8 [9 nil nil] nil] nil]))

(deftest test-right-child
  (testing "Testing zipper->right-child function")
  (are [node result] (= (zip/node (#'min-heap/zipper->right-child node)) result)
       (zip/vector-zip [4 nil nil]) nil
       (zip/vector-zip [4 [5 nil nil] [6 nil nil]]) [6 nil nil]
       (zip/vector-zip [4 [6 nil nil] [5 [8 [9 nil nil] nil] nil]]) [5 [8 [9 nil nil] nil] nil]))
