(ns rex.core-test
  (:require [rex.core :as cr]
            [rex.reducer :as rd]
            [rex.middleware :as mw]
            [rex.watcher :as swt]
            [rex.core-helpers :as h]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn setup! []
  (h/reset-core!))

(def simple-reducer
  (fn [state action]
    (let [old-value (get state :field [])
          event-value (get action :value :no-value)]
      (assoc-in state [:field] (conj old-value event-value)))))

(def conditional-reducer
  (fn [state action]
    (let [old-value (get state :field [])
          event-type (:type action)
          event-value (get action :value :no-value)]
      (if (= event-type :type-for-reducer)
        (assoc-in state [:field] (conj old-value event-value))
        state))))

(deftest reset-store-test
  (do
    (setup!)
    (is (= {} (cr/get-store)))))

(deftest dispatch-simple-event-test
  (do
    (setup!)
    (rd/defreducer simple-reducer)

    (cr/dispatch {:value :value1})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:value :value2})
    (is (= [:value1 :value2] (:field (cr/get-store))))

    (cr/dispatch {})
    (is (= [:value1 :value2 :no-value] (:field (cr/get-store))))))

(deftest dispatch-conditional-reducer-test
  (do
    (setup!)
    (rd/defreducer conditional-reducer)

    (cr/dispatch {:type :type-for-reducer
                  :value :value1})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:type :some-other-event-type
                  :value :value2})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:type :type-for-reducer
                  :value :value3})
    (is (= [:value1 :value3] (:field (cr/get-store))))))

(deftest dispatch-using-middlewares-test
  (do
    (setup!)
    (let [log-of-actions (atom [])]
      (mw/defmiddleware mw/id-middleware)
      (mw/defmiddleware
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

(deftest notify-watchers-on-change
  (do
    (setup!)
    (let [watch-vec (atom [])]

      (rd/defreducer conditional-reducer)

      (swt/defwatcher
        (fn [old-value action new-value]
          (swap! watch-vec conj [old-value new-value])))

      (cr/dispatch {:type :type-for-reducer
                    :value :value1})
      (is (= [[{} {:field [:value1]}]]
             @watch-vec))

      (cr/dispatch {:type :type-for-reducer
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
