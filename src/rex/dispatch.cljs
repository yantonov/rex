(ns rex.dispatch)

(defn- dispatch-using-reducers [get-store
                                update-store
                                get-reducers
                                get-subscribers
                                action]
  (let [old-store-value (get-store)]
    ;; TODO: single update
    (doseq [reducer (get-reducers)]
      (let [{name :name
             reduce-fn :fn} reducer]
        (update-store (fn [store-value]
                        (reduce-fn store-value action)))))
    (let [new-store-value (get-store)]
      (doseq [subscriber (get-subscribers)]
        (let [{callback :fn} subscriber]
          (callback old-store-value
                    new-store-value))))
    (get-store)))

(defn- dispatch-using-middlewares [get-store
                                   update-store
                                   get-reducers
                                   get-subscribers
                                   get-middlewares]
  (reduce (fn [accumulated-dispatch-fn middleware]
            (let [{name :name
                   middleware-fn :fn} middleware]
              (fn [action]
                (middleware-fn action
                               get-store
                               accumulated-dispatch-fn))))
          (partial dispatch-using-reducers
                   get-store
                   update-store
                   get-reducers
                   get-subscribers)
          (get-middlewares)))

(defn dispatch [get-store
                update-store
                get-reducers
                get-subscribers
                get-middlewares
                action]
  (let [dispatch-fn (dispatch-using-middlewares
                             get-store
                             update-store
                             get-reducers
                             get-subscribers
                             get-middlewares)]
    (dispatch-fn action)))
