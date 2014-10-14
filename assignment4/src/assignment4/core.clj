(ns assignment4.core
  (:require [assignment4.min-heap :refer :all])
  (:gen-class))

(def large-tree ["aing" ["bing" ["cing" nil nil] nil] ["ding" ["e" nil nil] nil]])
(defn -main
  [& args]
  ;(def large-tree (min-heap-insert large-tree "f"))
  ;(def large-tree (min-heap-insert large-tree 21))
  ;(def large-tree (min-heap-insert large-tree 22))
  (preorder-traversal large-tree))

