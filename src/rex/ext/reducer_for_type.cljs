(ns rex.ext.reducer-for-type
  (:require [rex.reducer :as r]))

(defn reducer-for-type
  ([name event-type reducer-fn]
   (r/defreducer name
     (fn [store action]
       (if (= event-type (:type action))
         (reducer-fn store action)
         store))))

  ([type reducer]
   (reducer-for-type nil type reducer)))
