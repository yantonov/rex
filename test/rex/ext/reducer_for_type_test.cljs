(ns rex.ext.reducer-for-type-test
  (:require [rex.ext.reducer-for-type :as sut]
            [cljs.test :refer-macros [deftest testing is are]]
            [rex.core :as cr]
            [rex.core-helpers :as h]))

(defn setup! []
  (h/reset-core!))

(deftest dispatch-only-for-action-with-interested-type
  (do
    (setup!)
    (sut/reducer-for-type
     :some-reducer
     :event-type-1
     (fn [state action]
       (let [old-field-value (get state :field [])
             event-type (get action :type :unknown-type)
             event-value (get action :value :no-value)]
         (if (= event-type)
           (assoc-in state [:field] (conj old-field-value event-value))
           state))))

    (cr/dispatch {:type :event-type-1
                  :value :value1})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:type :event-type-234
                  :value :value2})
    (is (= [:value1] (:field (cr/get-store))))

    (cr/dispatch {:type :event-type-1})
    (is (= [:value1 :no-value] (:field (cr/get-store))))))

