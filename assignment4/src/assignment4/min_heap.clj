"Node is representated as [key left right height]"

(ns assignment4.min-heap
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

(defn- zipper->height
  [zipper]
  (when (zip/node zipper)
    (-> zipper
        zip/down
        zip/right
        zip/right
        zip/right
        zip/node)))

(defn- zipper->value
  [zipper]
  (if (zip/node zipper)
    (-> zipper
        zip/down
        zip/node)
    nil))

(defn- replace-node
  [zipper replacement height]
  (let [location (zip/node zipper)
        node (zip/make-node zipper location [replacement nil nil height])]
    (-> zipper
        (zip/replace node)
        zip/root)))

(defn tree-empty?
  [zipper]
  (not (zip/node zipper)))

(defn- min-heap-zipper-insert
  [zipper x height]
  (let [value (zipper->value zipper)
        node-height (zipper->height zipper)]
    (cond
     (tree-empty? zipper) (replace-node zipper x height)
     (= x value) (zip/root zipper)
     (< x value) (recur (zipper->left-child zipper) x (+ node-height 1))
     (> x value) (recur (zipper->right-child zipper) x (+ node-height 1)))))

(defn min-heap-insert
  [tree x]
  (min-heap-zipper-insert (zip/vector-zip tree) x 0))

(defn swap-value
  [zipper value]
  (zip/edit (zip/vector-zip zipper) value))

(defn preorder-zip-traversal
  [zipper]
  (when (zip/node zipper)
    (println "Value: " (zipper->value zipper) "Height: " (zipper->height zipper))
    (preorder-zip-traversal (zipper->left-child zipper))
    (preorder-zip-traversal (zipper->right-child zipper))))

(defn preorder-traversal
  [tree]
  (tmp-zip (zip/vector-zip tree)))

















