(ns rex.middleware
  (:require [rex.core :as c]))

(defn reset-middlewares! []
  (reset! c/middlewares c/*reducers-init-value*))

(defn defmiddleware
  "register middleware with name (for debug purposes) or without it"
  ([name middleware]
   (swap! c/middlewares conj {:name name
                              :fn middleware}))
  ([middleware]
   (defmiddleware nil middleware)))

(defn id-middleware
  "trivial middleware, just delegate to next dispatch function"
  [store next-dispatch-fn cursor action]
  (next-dispatch-fn cursor action))
