(ns rex.dispatch)

(defn- dispatch-event-internal [get-store
                                update-store
                                get-reducers
                                get-subscribers
                                deref-state-by-cursor
                                cursor
                                event]
  (let [event-type (get event :type :unknown)
        old-store-value (get-store)]
    (doseq [r (get-reducers)]
      (let [{name :name
             reduce-rn :fn} r]
        (update-store (fn [store-value]
                        (reduce-rn store-value
                                   event-type
                                   event
                                   cursor)))))
    (let [new-store-value (get-store)]
      (doseq [subscriber (get-subscribers)]
        (let [{cursor :cursor
               callback :fn} subscriber
               new-zoomed-value (deref-state-by-cursor cursor new-store-value)
               old-zoomed-value (deref-state-by-cursor cursor old-store-value)]
          (if (not (= new-zoomed-value old-zoomed-value))
            (callback new-zoomed-value)))))
    (get-store)))

(defn- dispatch-event-with-middlewares [get-store
                                        update-store
                                        get-reducers
                                        get-middlewares
                                        get-subscribers
                                        deref-state-by-cursor]
  (reduce (fn [accumulated-dispatch-event-fn middleware]
            (let [{name :name
                   middleware-fn :fn} middleware]
              (fn [cursor action]
                (middleware-fn cursor
                               action
                               get-store
                               accumulated-dispatch-event-fn))))
          (partial dispatch-event-internal
                   get-store
                   update-store
                   get-reducers
                   get-subscribers
                   deref-state-by-cursor)
          (get-middlewares)))

(defn dispatch-event [get-store
                      update-store
                      get-reducers
                      get-middlewares
                      get-subscribers
                      deref-state-by-cursor
                      cursor
                      event]
  (let [decorator (dispatch-event-with-middlewares get-store
                                                   update-store
                                                   get-reducers
                                                   get-middlewares
                                                   get-subscribers
                                                   deref-state-by-cursor)]
    (decorator cursor event)))

(defn dispatch [get-store
                update-store
                get-reducers
                get-middlewares
                get-subscribers
                deref-state-by-cursor
                cursor
                action-creator]
  (let [dispatch-event-fn (partial dispatch-event
                                   get-store
                                   update-store
                                   get-reducers
                                   get-middlewares
                                   get-subscribers
                                   deref-state-by-cursor)]
    (let [event (action-creator dispatch-event-fn
                                get-store
                                cursor)]
      (if (nil? event)
        (get-store)
        (dispatch-event-fn cursor event)))))
