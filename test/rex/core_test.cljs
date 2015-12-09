(ns rex.core-test
  (:require [rex.core :as sut]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-store-test
  (do
    (sut/reset-store!)
    (is (= {} @sut/store))))

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

(defn create-event [type value]
  {:type type
   :value value})

(deftest dispatch-event-test
  (do
    (sut/reset-store!)
    (sut/reset-reducers!)
    (sut/defreducer :some-reducer
      (fn [state
           event-type
           event
           cursor]
        (let [old-value (get state :field [])
              event-value (get event :value :no-value)]
          (assoc-in state [:field] (conj old-value event-value)))))
    (sut/dispatch-event nil
                        (create-event :some-event-type :value1))
    (is (= [:value1] (:field @sut/store)))
    (sut/dispatch-event nil
                        (create-event :other-event-type :value2))
    (is (= [:value1 :value2] (:field @sut/store)))))
