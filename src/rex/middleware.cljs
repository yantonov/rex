(ns rex.middleware)

(defonce *middlewares-init-value* [])
(defonce middlewares (atom *middlewares-init-value*))

(defn reset-middlewares! []
  (reset! middlewares *middlewares-init-value*))

(defn get-middlewares []
  @middlewares)

(defn defmiddleware
  "register middleware with name (for debug purposes) or without it"
  ([name middleware]
   (swap! middlewares conj {:name name
                              :fn middleware}))
  ([middleware]
   (defmiddleware nil middleware)))

(defn id-middleware
  "trivial middleware, just delegate to next dispatch function"
  [store next-dispatch-fn cursor action]
  (next-dispatch-fn cursor action))
