"Node is representated as [key left right]"

(ns assignment4.min-heap
  (:require [clojure.tools.trace :refer :all])
  (:require [clojure.zip :as zip])
  (:gen-class))

(defn- zipper->left-child
  [zipper]
  (-> zipper
      zip/down
      zip/right))

(defn- zipper->right-child
  [zipper]
  (-> zipper
      zip/down
      zip/right
      zip/right))

(defn- zipper->value
  [zipper]
  (if (zip/node zipper)
    (-> zipper
        zip/down
        zip/node)
    nil))

(defn- replace-node
  [zipper replacement]
  (let [location (zip/node zipper)
        node (zip/make-node zipper location [replacement nil nil])]
    (-> zipper
        (zip/replace node)
        zip/root)))

(defn tree-empty?
  [zipper]
  (not (zip/node zipper)))

(defn- swap-value
  [zipper value]
  (-> zipper
      (zip/edit (fn [node]
              (assoc-in node [0] value)))
      zip/node))

(defn- node-height
  [zipper]
  (if (tree-empty? zipper)
    0
   (let [left-child-height (+ 1 (node-height (zipper->left-child zipper)))
         right-child-height (+ 1 (node-height (zipper->right-child zipper)))]
     (if (> left-child-height right-child-height)
       left-child-height
       right-child-height))))

(defn- min-heap-zipper-insert
  [zipper x]
  (if (tree-empty? zipper)
    (replace-node zipper x)
    (let [value (zipper->value zipper)
        left-height (node-height (zipper->left-child zipper))
        right-height (node-height (zipper->right-child zipper))]
    ;(println "Value: " value)
    ;(println "Left-height: " left-child-height)
    ;(println "Right-height: " right-child-height)
    (cond
     (tree-empty? zipper) (replace-node zipper x)
     (= x value) (zip/root zipper)
     (< x value) (recur (swap-value zipper x) value)
     (<= left-height right-height) (recur (zipper->left-child zipper) x)
     (< right-height left-height) (recur (zipper->right-child zipper) x )))))

(defn min-heap-insert
  [tree x]
  (min-heap-zipper-insert (zip/vector-zip tree) x))

(defn preorder-zipper-traversal
  [zipper]
  (when (zip/node zipper)
    (print "  " (zipper->value zipper))
    (preorder-zipper-traversal (zipper->left-child zipper))
    (preorder-zipper-traversal (zipper->right-child zipper))))

(defn preorder-traversal
  [tree]
  (preorder-zipper-traversal (zip/vector-zip tree))
  (println ""))















