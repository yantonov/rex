(ns rex.core)

(defonce *store-init-value* {})
(defonce store (atom *store-init-value*))

(defonce *reducers-init-value* [])
(defonce reducers (atom *reducers-init-value*))

(defonce *middlewares-init-value* [])
(defonce middlewares (atom *middlewares-init-value*))

(defn reset-store! []
  (reset! store *store-init-value*))

(defn reset-middlewares! []
  (reset! middlewares *reducers-init-value*))

(defn defmiddleware
  "register middleware with name (for debug purposes) or without it"
  ([name middleware]
   (swap! middlewares conj {:name name
                            :fn middleware}))
  ([middleware]
   (defmiddleware nil middleware)))

(defn- dispatch-event [cursor event]
  (let [event-type (get event :type :unknown)]
    (doseq [r @reducers]
      (let [{name :name
             reduce-rn :fn} r]
        ;; todo think about it
        ;; maybe reduce + single swap
        (swap! store reduce-rn event-type event cursor))))
  @store)

(defn- dispatch-event-with-middlewares []
  (let [store-value-getter (fn [] @store)]
    (reduce (fn [accumulated-dispatch-event-fn middleware]
              (let [{name :name
                     middleware-fn :fn} middleware]
                (fn [cursor action]
                  (middleware-fn store-value-getter
                                 accumulated-dispatch-event-fn
                                 cursor
                                 action))))
            dispatch-event
            @middlewares)))

(defn dispatch [cursor action-creator]
  (let [dispatch-event-fn (dispatch-event-with-middlewares)]
    (let [event (action-creator (partial dispatch-event-fn cursor)
                                @store
                                cursor)]
      (if (nil? event)
        @store
        (dispatch-event-fn cursor event)))))
