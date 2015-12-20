(ns rex.core-test
  (:require [rex.core :as cr]
            [rex.cursor :as cur]
            [rex.reducer :as rd]
            [rex.middleware :as mw]
            [rex.subscriber :as sb]
            [rex.helpers :as hp]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn setup! []
  (cr/reset-store!)
  (rd/reset-reducers!)
  (mw/reset-middlewares!)
  (sb/reset-subscribers!))

(deftest reset-store-test
  (do
    (setup!)
    (is (= {} (cr/get-store)))))

(deftest dispatch-event-test
  (do
    (setup!)
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
    (setup!)
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
    (setup!)
    (let [log-of-actions (atom [])]
      (mw/defmiddleware :log-action (fn [cursor action store next-dispatch-fn]
                                      (do
                                        (swap! log-of-actions conj action)
                                        (next-dispatch-fn cursor action))))
      (cr/dispatch nil (hp/test-action-creator :some-event-type
                                               :some-value))
      (is (= [{:type :some-event-type
               :value :some-value}]
             @log-of-actions)))))

(deftest notify-subscribers-on-change
  (do
    (setup!)

    (let [watch-vec (atom [])]

      (rd/defreducer :some-reducer
        (fn [state
             event-type
             event
             cursor]
          (let [old-value (get state :field [])
                event-value (get event :value :no-value)]
            (if (= event-type :some-event-type)
              (assoc state :field (conj old-value event-value))
              state))))

      (sb/defsubscriber (cur/nest (cur/make-cursor) :field)
        (fn [store-value]
          (swap! watch-vec conj store-value)))

      (cr/dispatch nil (hp/test-action-creator :some-event-type :value1))
      (is (= [[:value1]]
             @watch-vec))

      (cr/dispatch nil (hp/test-action-creator :some-event-type :value2))
      (is (= [[:value1]
              [:value1 :value2]]
             @watch-vec))

      ;; no changes under cursor - no callback calls for subscriber
      (cr/dispatch nil (hp/test-action-creator :other-event-type :other-value))
      (is (= [[:value1]
              [:value1 :value2]]
             @watch-vec)))))
