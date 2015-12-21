(ns rex.ext.action-creator-test
  (:require [rex.ext.action-creator :as sut]
            [cljs.test :refer-macros [deftest testing is are]]
            [rex.core :as cr]
            [rex.reducer :as rd]
            [rex.middleware :as mw]
            [rex.subscriber :as sb]))

(defn setup! []
  (cr/reset-store!)
  (rd/reset-reducers!)
  (mw/reset-middlewares!)
  (sb/reset-subscribers!))

(deftest composite-action-test
  (setup!)

  (mw/defmiddleware :action-creator-middleware
    sut/action-creator-middleware)

  (rd/defreducer :some-reducer
    (fn [state action]
      (let [old-field-value (get state :field [])
            event-value (get action :value :no-value)]
        (assoc-in state [:field] (conj old-field-value event-value)))))

  (cr/dispatch
   (fn [dispatch-fn get-store]
     (dispatch-fn {:value :value1})
     (dispatch-fn {:value :value2})))

  (is (= [:value1 :value2] (:field (cr/get-store)))))

(deftest composite-action-can-get-store-state
  (setup!)

  (mw/defmiddleware :action-creator-middleware
    sut/action-creator-middleware)

  (rd/defreducer :some-reducer
    (fn [state action]
      (let [old-field-value (get state :field [])
            event-value (get action :value :no-value)]
        (assoc-in state [:field] (conj old-field-value event-value)))))

  (let [before (atom nil)
        middle (atom nil)
        after (atom nil)]
    (cr/dispatch
     (fn [dispatch-fn get-store]
       (reset! before (get-store))
       (dispatch-fn {:value :value1})
       (reset! middle (get-store))
       (dispatch-fn {:value :value2})
       (reset! after (get-store))))

    (is (= {} @before))
    (is (= {:field [:value1]} @middle))
    (is (= {:field [:value1 :value2]} @after))))
