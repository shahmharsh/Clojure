(ns assignment4.core
  (:require [assignment4.min-heap :as min-heap :refer :all]
            [clojure.walk :refer :all])
  (:gen-class))

(def numbers-heap [4 [10 [20 nil nil] nil] [5 [13 nil nil] nil]])
(def string-heap ["aing" ["bing" ["c" nil nil] nil] ["ding" ["e" nil nil] nil]])

(defn -main
  [& args]
  (min-heap/print-preorder numbers-heap)
  (min-heap/print-preorder string-heap)
  (min-heap/print-ing-words-preorder string-heap))
