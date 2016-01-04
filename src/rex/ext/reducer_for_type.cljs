(ns rex.ext.reducer-for-type
  (:require [rex.reducer :as r]))

(defn reducer-for-type
  [event-type reducer-fn]
  (r/defreducer
    (fn [store action]
      (if (= event-type (:type action))
        (reducer-fn store action)
        store))))
