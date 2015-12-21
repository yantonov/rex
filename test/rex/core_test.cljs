(ns rex.core-test
  (:require [rex.core :as cr]
            [rex.reducer :as rd]
            [rex.middleware :as mw]
            [rex.subscriber :as sb]
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

(deftest dispatch-simple-event-test
  (do
    (setup!)
    (rd/defreducer :some-reducer
      (fn [state action]
        (let [old-field-value (get state :field [])
              event-value (get action :value :no-value)]
          (assoc-in state [:field] (conj old-field-value event-value)))))

    (cr/dispatch {:value :value1})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:value :value2})
    (is (= [:value1 :value2] (:field (cr/get-store))))

    (cr/dispatch {})
    (is (= [:value1 :value2 :no-value] (:field (cr/get-store))))))

(deftest dispatch-conditional-reducer-test
  (do
    (setup!)
    (rd/defreducer :some-reducer
      (fn [state action]
        (let [old-value (get state :field [])
              event-type (get action :type)
              event-value (get action :value :no-value)]
          (if (= event-type :type1)
            (assoc-in state [:field] (conj old-value event-value))
            state))))

    (cr/dispatch {:type :type1
                  :value :value1})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:type :type234
                  :value :value2})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:type :type1
                  :value :value3})
    (is (= [:value1 :value3] (:field (cr/get-store))))))

(deftest dispatch-using-middlewares-test
  (do
    (setup!)
    (let [log-of-actions (atom [])]
      (mw/defmiddleware :log-action
        (fn [action store next-dispatch-fn]
          (do
            (swap! log-of-actions conj action)
            (next-dispatch-fn action))))
      (cr/dispatch {:type :event-type
                    :value :some-value})
      (is (= [{:type :event-type
               :value :some-value}]
             @log-of-actions))
      (is (= {}  (cr/get-store)))

      (cr/dispatch {:type :event-type2
                    :value :some-value2})
      (is (= [{:type :event-type
               :value :some-value}
              {:type :event-type2
               :value :some-value2}]
             @log-of-actions))
      (is (= {}  (cr/get-store))))))

(deftest notify-subscribers-on-change
  (do
    (setup!)
    (let [watch-vec (atom [])]

      (rd/defreducer :some-reducer
        (fn [state
             action]
          (let [old-value (get state :field [])
                event-type (:type action)
                event-value (get action :value :no-value)]

            (if (= event-type :some-event-type)
              (assoc state :field (conj old-value event-value))
              state))))

      (sb/defsubscriber
        (fn [old-value new-value]
          (swap! watch-vec conj [old-value new-value])))

      (cr/dispatch {:type :some-event-type
                    :value :value1})
      (is (= [[{} {:field [:value1]}]]
             @watch-vec))

      (cr/dispatch {:type :some-event-type
                    :value :value2})
      (is (= [[{} {:field [:value1]}]
              [{:field [:value1]} {:field [:value1 :value2]}]]
             @watch-vec))

      (cr/dispatch {:type :other-event-type
                    :other-value :other-value})
      (is (= [[{} {:field [:value1]}]
              [{:field [:value1]} {:field [:value1 :value2]}]
              [{:field [:value1 :value2]} {:field [:value1 :value2]}]]
             @watch-vec)))))
