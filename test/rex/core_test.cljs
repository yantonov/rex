(ns rex.core-test
  (:require [rex.core :as c]
            [rex.reducer :as r]
            [rex.helpers :as h]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-store-test
  (do
    (c/reset-store!)
    (is (= {} @c/store))))

(deftest dispatch-event-test
  (do
    (c/reset-store!)
    (r/reset-reducers!)
    (r/defreducer :some-reducer
      (fn [state
           event-type
           event
           cursor]
        (let [old-value (get state :field [])
              event-value (get event :value :no-value)]
          (assoc-in state [:field] (conj old-value event-value)))))

    (c/dispatch-event nil (h/create-event :some-event-type :value1))
    (is (= [:value1] (:field @c/store)))

    (c/dispatch-event nil (h/create-event :other-event-type :value2))
    (is (= [:value1 :value2] (:field @c/store)))))

(deftest dispatch-test
  (do
    (c/reset-store!)
    (r/reset-reducers!)
    (r/defreducer :some-reducer
      (fn [state
           event-type
           event
           cursor]
        (let [old-value (get state :field [])
              event-value (get event :value :no-value)]
          (assoc-in state [:field] (conj old-value event-value)))))

    (c/dispatch nil (h/test-action-creator :some-event-type :value1))
    (is (= [:value1] (:field @c/store)))

    (c/dispatch nil (h/test-action-creator :some-event-type :value2))
    (is (= [:value1 :value2] (:field @c/store)))))
