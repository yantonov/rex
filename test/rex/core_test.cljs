(ns rex.core-test
  (:require [rex.core :as sut]
            [rex.helpers :as h]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-store-test
  (do
    (sut/reset-store!)
    (is (= {} @sut/store))))

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
                        (h/create-event :some-event-type :value1))
    (is (= [:value1] (:field @sut/store)))
    (sut/dispatch-event nil
                        (h/create-event :other-event-type :value2))
    (is (= [:value1 :value2] (:field @sut/store)))))

(deftest dispatch-test
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
    (sut/dispatch nil (h/test-action-creator :some-event-type :value1))
    (is (= [:value1] (:field @sut/store)))
    (sut/dispatch nil (h/test-action-creator :some-event-type :value2))
    (is (= [:value1 :value2] (:field @sut/store)))))
