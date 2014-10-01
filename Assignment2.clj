; 1. Write a non recursive function sdsu-nth which returns the nth element from a sequence.
;    You are not allowed to use the functions nth, get or use the sequence as a function. So we
;    get
(defn sdsu-nth
  [input-list n]
  (if (zero? (count input-list))
    (throw (Exception. "IndexOutOfBoundsException"))
    (if (>= n (count input-list))
      (throw (Exception. "IndexOutOfBoundsException"))
      (if (neg? n)
        (throw (Exception. "IndexOutOfBoundsException"))
        (last (take (+ n 1) input-list))))))


(= (sdsu-nth '(6 5 4 3) 2) 4)
(= (sdsu-nth [:a :b :c] 1) :b)
(= (sdsu-nth '([1 2] [3 4] [5 6]) 2) [5 6])





; 2. Write a recursive function r-sdsu-nth which returns the nth element from a sequence. You
;    are not allowed to use the functions nth, get or use the sequence as a function.
(defn r-sdsu-nth
  [input-list n]
  (if (empty? input-list)
    (throw (Exception. "IndexOutOfBoundsException"))
    (if (= n 0)
      (first input-list)
      (r-sdsu-nth (rest input-list) (- n 1)))))


(= (r-sdsu-nth '(6 5 4 3) 2) 4)
(= (r-sdsu-nth [:a :b :c] 1) :b)
(= (r-sdsu-nth '([1 2] [3 4] [5 6]) 2) [5 6])





; 3. Write a recursive function sdsu-reverse which reverses a sequence. You are not allowed to
;    use reverse or rseq.
(declare helper-reverse)
(defn sdsu-reverse
  [input-sequence]
  (helper-reverse input-sequence nil))

(defn- helper-reverse
  [input-sequence reverse-sequence]
  (if (empty? input-sequence)
    reverse-sequence
    (helper-reverse (rest input-sequence) (cons (first input-sequence) reverse-sequence))))


(= (sdsu-reverse [1 2 3 4]) '(4 3 2 1))
(= (sdsu-reverse '(1)) '(1))
(= (sdsu-reverse '([1 2] [3 4] [5 6])) '([5 6] [3 4] [1 2]))





; 4. Write a function sdsu-dup that will duplicate each element in a sequence.
(declare helper-dup)
(defn sdsu-dup
  [input-list]
  (helper-dup input-list nil))

(defn- helper-dup
  [input-list output-list]
  (if (empty? input-list)
    output-list
    (helper-dup (rest input-list) (concat output-list (cons (first input-list) (cons (first input-list) nil))))))


(= (sdsu-dup [1 2 3]) '(1 1 2 2 3 3))
(= (sdsu-dup [:a :a :b :b]) '(:a :a :a :a :b :b :b :b))
(= (sdsu-dup [[1 2] [3 4]]) '([1 2] [1 2] [3 4] [3 4]))





; 5. Write a function sdsu-no-dup that will remove consecutive duplicates from a sequence.
(declare helper-no-dup)
(defn sdsu-no-dup
  [input-list]
  (helper-no-dup input-list nil))

(defn- helper-no-dup
  [input-list output-list]
  (if (empty? input-list)
    output-list
    (if (= (first input-list) (first (rest input-list)))
      (helper-no-dup (rest input-list) output-list)
      (helper-no-dup (rest input-list) (concat output-list (cons (first input-list) nil))))))


(= (sdsu-no-dup [1 1 2 3 3 2 2 3]) '(1 2 3 2 3))
(= (sdsu-no-dup [[1 2] [1 2] [3 4] [1 2]]) '([1 2] [3 4] [1 2]))





; 6. Write a function sdsu-pack that separates consecutive duplicates in a sequence into sub-lists.
(declare helper-pack)
(defn sdsu-pack
  [input-list]
  (helper-pack (reverse input-list) nil nil))

(defn- helper-pack
  [input-list output-list duplicate-list]
  (if (empty? input-list)
    output-list
    (if (= (first input-list) (first (rest input-list)))
      (helper-pack (rest input-list) output-list (cons (first input-list) duplicate-list))
      (helper-pack (rest input-list) (conj output-list (cons (first input-list) duplicate-list)) nil))))


(= (sdsu-pack [1 1 2 1 1 1 3 3]) '((1 1) (2) (1 1 1) (3 3)))
(= (sdsu-pack [:a :a :b :b :c]) '((:a :a) (:b :b) (:c)))
