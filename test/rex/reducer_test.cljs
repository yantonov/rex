(ns rex.reducer-test
  (:require [rex.reducer :as r]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-reducers-test
  (do
    (r/reset-reducers!)
    (is (= [] (r/get-reducers)))))

(deftest defreducer-with-name-test
  (do
    (r/reset-reducers!)
    (r/defreducer :r1 r/id-reducer)
    (let [reducers (r/get-reducers)]
      (is (= 1 (count reducers)))
      (let [reducer (first reducers)]
        (is (= :r1 (:name reducer)))
        (is (not (nil? (:fn reducer))))))))

(deftest defreducer-without-name-test
  (do
    (r/reset-reducers!)
    (r/defreducer r/id-reducer)
    (let [reducers (r/get-reducers)]
      (is (= 1 (count reducers)))
      (let [reducer (first reducers)]
        (is (nil? (:name reducer)))
        (is (not (nil? (:fn reducer))))))))
