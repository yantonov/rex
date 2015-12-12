(ns rex.core-test
  (:require [rex.core :as cr]
            [rex.reducer :as rd]
            [rex.middleware :as mw]
            [rex.helpers :as hp]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-store-test
  (do
    (cr/reset-store!)
    (is (= {} (cr/get-store)))))

(deftest dispatch-event-test
  (do
    (cr/reset-store!)
    (rd/reset-reducers!)
    (rd/defreducer :some-reducer
      (fn [state
           event-type
           event
           cursor]
        (let [old-value (get state :field [])
              event-value (get event :value :no-value)]
          (assoc-in state [:field] (conj old-value event-value)))))

    (cr/dispatch-event nil (hp/create-event :some-event-type :value1))
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch-event nil (hp/create-event :other-event-type :value2))
    (is (= [:value1 :value2] (:field (cr/get-store))))))

(deftest dispatch-test
  (do
    (cr/reset-store!)
    (rd/reset-reducers!)
    (rd/defreducer :some-reducer
      (fn [state
           event-type
           event
           cursor]
        (let [old-value (get state :field [])
              event-value (get event :value :no-value)]
          (assoc-in state [:field] (conj old-value event-value)))))

    (cr/dispatch nil (hp/test-action-creator :some-event-type :value1))
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch nil (hp/test-action-creator :some-event-type :value2))
    (is (= [:value1 :value2] (:field (cr/get-store))))))

(deftest dispatch-using-middlewares-test
  (do
    (mw/reset-middlewares!)
    (let [log-of-actions (atom [])]
      (mw/defmiddleware :log-action (fn [store next cursor action]
                                      (do
                                        (swap! log-of-actions conj action)
                                        (next cursor action))))
      (cr/dispatch nil (hp/test-action-creator :some-event-type
                                               :some-value))
      (is (= [{:type :some-event-type
               :value :some-value}]
             @log-of-actions)))))
