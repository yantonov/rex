(ns rex.dispatch)

(defn- dispatch-event-internal [get-store
                                update-store
                                get-reducers
                                cursor
                                event]
  (let [event-type (get event :type :unknown)]
    (doseq [r (get-reducers)]
      (let [{name :name
             reduce-rn :fn} r]
        (update-store (fn [store-value]
                        (reduce-rn store-value
                                   event-type
                                   event
                                   cursor))))))
  (get-store))

(defn- dispatch-event-with-middlewares [get-store
                                        update-store
                                        get-reducers
                                        get-middlewares]
  (reduce (fn [accumulated-dispatch-event-fn middleware]
            (let [{name :name
                   middleware-fn :fn} middleware]
              (fn [cursor action]
                (middleware-fn get-store
                               accumulated-dispatch-event-fn
                               cursor
                               action))))
          (partial dispatch-event-internal
                   get-store
                   update-store
                   get-reducers)
          (get-middlewares)))

(defn dispatch-event [get-store
                      update-store
                      get-reducers
                      get-middlewares
                      cursor
                      event]
  (let [decorator (dispatch-event-with-middlewares get-store
                                                   update-store
                                                   get-reducers
                                                   get-middlewares)]
    (decorator cursor event)))

(defn dispatch [get-store
                update-store
                get-reducers
                get-middlewares
                cursor
                action-creator]
  (let [dispatch-event-fn (partial dispatch-event
                                   get-store
                                   update-store
                                   get-reducers
                                   get-middlewares)]
    (let [event (action-creator (partial dispatch-event-fn cursor)
                                (get-store)
                                cursor)]
      (if (nil? event)
        (get-store)
        (dispatch-event-fn cursor event)))))
