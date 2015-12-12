(ns rex.core)

(defonce *store-init-value* {})
(defonce store (atom *store-init-value*))

(defonce *reducers-init-value* [])
(defonce reducers (atom *reducers-init-value*))

(defonce *middlewares-init-value* [])
(defonce middlewares (atom *middlewares-init-value*))

(defn reset-store! []
  (reset! store *store-init-value*))

(defn- dispatch-event-internal [cursor event]
  (let [event-type (get event :type :unknown)]
    (doseq [r @reducers]
      (let [{name :name
             reduce-rn :fn} r]
        ;; todo think about it
        ;; maybe reduce + single swap
        (swap! store reduce-rn event-type event cursor))))
  @store)

(defn- dispatch-event-with-middlewares [store-value-getter
                                        middlewares-getter]
  (reduce (fn [accumulated-dispatch-event-fn middleware]
            (let [{name :name
                   middleware-fn :fn} middleware]
              (fn [cursor action]
                (middleware-fn store-value-getter
                               accumulated-dispatch-event-fn
                               cursor
                               action))))
          dispatch-event-internal
          (middlewares-getter)))

(defn dispatch-event [cursor event]
  (let [decorator (dispatch-event-with-middlewares (fn [] @store)
                                                   (fn [] @middlewares))]
    (decorator cursor event)))

(defn dispatch [cursor action-creator]
  (let [dispatch-event-fn dispatch-event]
    (let [event (action-creator (partial dispatch-event-fn cursor)
                                @store
                                cursor)]
      (if (nil? event)
        @store
        (dispatch-event-fn cursor event)))))
