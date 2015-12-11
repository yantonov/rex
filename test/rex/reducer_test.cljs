(ns rex.reducer-test
  (:require [rex.core :as sut]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-reducers-test
  (do
    (sut/reset-reducers!)
    (is (= [] @sut/reducers))))

(deftest defreducer-with-name-test
  (do
    (sut/reset-reducers!)
    (sut/defreducer :r1 (fn [] 1))
    (let [reducers @sut/reducers]
      (is (= 1 (count reducers)))
      (is (= :r1 (-> reducers
                     first
                     :name)))
      (is (not (nil? (-> reducers
                         first
                         :fn)))))))

(deftest defreducer-without-name-test
  (do
    (sut/reset-reducers!)
    (sut/defreducer (fn [] 1))
    (let [reducers @sut/reducers]
      (is (= 1 (count reducers)))
      (is (nil? (-> reducers
                    first
                    :name)))
      (is (not (nil? (-> reducers
                         first
                         :fn)))))))
