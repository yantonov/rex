(ns rex.ext.reducer-for-event
  (:require [rex.reducer :as r]))

(defn reducer-for-type
  ([name event-type reducer-fn]
   (r/defreducer name
     (fn [store action]
       (if (= event-type (:type action))
         (reducer-fn store action)
         store))))

  ([type reducer]
   (recucer-for-type nil type reducer)))
