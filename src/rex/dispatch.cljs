(ns rex.dispatch)

(defn- dispatch-using-reducers [get-store
                                update-store
                                get-reducers
                                get-watchers
                                action]
  (let [old-store-value (get-store)]
    ;; TODO: single update
    (doseq [reducer (get-reducers)]
      (let [{name :name
             reduce-fn :fn} reducer]
        (update-store (fn [store-value]
                        (reduce-fn store-value action)))))
    (let [new-store-value (get-store)]
      (doseq [watcher (get-watchers)]
        (let [{callback :fn} subscriber]
          (callback old-store-value
                    new-store-value))))
    (get-store)))

(defn- dispatch-using-middlewares [get-store
                                   update-store
                                   get-reducers
                                   get-watchers
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
                   get-watchers)
          (get-middlewares)))

(defn dispatch [get-store
                update-store
                get-reducers
                get-watchers
                get-middlewares
                action]
  (let [dispatch-fn (dispatch-using-middlewares
                     get-store
                     update-store
                     get-reducers
                     get-watchers
                     get-middlewares)]
    (dispatch-fn action)))
