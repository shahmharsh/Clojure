
;Working Code		30/30
;
;Unit Tests		10/10
;
;Comments		10/10
;
;Quality of Code		9/10
;min-heap-zipper-insert complex
;/60


"Node is representated as [key left right]
 Assumption: User wont mix strings and numbers"

(ns assignment4.min-heap
  (:require [clojure.tools.trace :refer :all])
  (:require [clojure.zip :as zip])
  (:gen-class))

;zipper->left-child and zipper->right-child will never be called with non-zip node
(defn- zipper->left-child
  "Returns left child of node.
  Expects a clojure.zip/node as input."
  [zipper]
  (-> zipper
      zip/down
      zip/right))

(defn- zipper->right-child
  "Returns right child of node.
  Expects a clojure.zip/node as input."
  [zipper]
  (-> zipper
      zip/down
      zip/right
      zip/right))

(defn- zipper->value
  "Returns value stored in node or nil"
  [zipper]
  (if (zip/node zipper)
    (-> zipper
        zip/down
        zip/node)
    nil))

(defn- replace-node
  "Replaces passed in node (zipper) with new node
  which has value 'replacement' and no left or right child.
  Returns root of heap after replacing node.
  Expects a clojure.zip/node as input."
  [zipper replacement]
  (let [location (zip/node zipper)
        node (zip/make-node zipper location [replacement nil nil])]
    (-> zipper
        (zip/replace node)
        zip/root)))

(defn- tree-empty?
  "Helper function to check if tree is empty.
  Expects a clojure.zip/node as input."
  [zipper]
  (not (zip/node zipper)))

(defn- swap-value
  "Swaps node data with parameter value and returns the node.
  Expects a clojure.zip/node as input."
  [zipper value]
  (-> zipper
      (zip/edit (fn [node]
              (assoc-in node [0] value)))))

(defn- node-height
  "Returns height of the node.
  Expects a clojure.zip/node as input."
  [zipper]
  (if (tree-empty? zipper)
    0
   (let [left-child-height (+ 1 (node-height (zipper->left-child zipper)))
         right-child-height (+ 1 (node-height (zipper->right-child zipper)))]
     (if (> left-child-height right-child-height)
       left-child-height
       right-child-height))))

(defn- min-heap-zipper-insert
  "Returns the root of the heap with parameter x inserted into the heap
  Duplicates are not allowed, x is compared with every element along the path to insert and if
  larger element is encountered, swap x and value of the element and continues inserting
  the value of the element along same path"
  [zipper x]
  (if (tree-empty? zipper)
    (replace-node zipper x)
    (let [value (zipper->value zipper)
          compare-result (compare (zipper->value zipper) x)
          left-height (node-height (zipper->left-child zipper))
          right-height (node-height (zipper->right-child zipper))]
      (cond
       (zero? compare-result) (zip/root zipper)
       (pos? compare-result) (recur (swap-value zipper x) value)
       (<= left-height right-height) (recur (zipper->left-child zipper) x)
       (< right-height left-height) (recur (zipper->right-child zipper) x )))))

(defn insert
  "Wrapper function to insert values into the heap"
  [tree x]
  (if (empty? tree)
    [x nil nil]
    (min-heap-zipper-insert (zip/vector-zip tree) x)))

(defn- preorder-zipper-traversal
  "Returns a list of values in the heap in preorder"
  [zipper]
  (when (zip/node zipper)
    (let [left-list (preorder-zipper-traversal (zipper->left-child zipper))
          right-list (preorder-zipper-traversal(zipper->right-child zipper))
          current-value (zipper->value zipper)]
       (cons current-value (concat left-list right-list)))))


(defn preorder-traversal
  "Wrapper function to traverse tree in preorder"
  [tree]
  (if (empty? tree)
    []
    (preorder-zipper-traversal (zip/vector-zip tree))))

(defn print-preorder
  "Prints preorder list to console"
  [tree]
  (println (preorder-traversal tree)))

(defn- ends-in-ing?
  "Helper function to test if string ends in ing"
  [element]
  (.endsWith (clojure.string/lower-case element) "ing"))

(defn print-ing-words-preorder
  "Prints preorder list ending in ing to console"
  [tree]
  (let [preorder-list (preorder-traversal tree)]
    (println (filter ends-in-ing? preorder-list))))








