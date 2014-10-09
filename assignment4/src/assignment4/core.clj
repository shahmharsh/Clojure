(ns assignment4.core
  (:require [assignment4.min-heap :refer :all])
  (:gen-class))

(def large-tree [10 [5 [4 nil nil 2] [8 nil nil 2] 1] [20 nil nil 1] 0])
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (preorder-traversal large-tree))

