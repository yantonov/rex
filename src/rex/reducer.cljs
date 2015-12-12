(ns rex.reducer
  (:require [rex.core :as c]))

(defn reset-reducers! []
  (reset! c/reducers c/*reducers-init-value*))

(defn defreducer
  "register reducer with name (for debug purposes) or without it"
  ([name reduce-fn]
   (swap! c/reducers conj {:name name
                         :fn reduce-fn}))
  ([reduce-fn]
   (defreducer nil reduce-fn)))

(defn id-reducer
  "trivial reducer, returns state as is"
  [store event-type event cursor]
  store)
