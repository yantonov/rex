(ns rex.core)

(defonce *store-init-value* {})
(defonce store (atom *store-init-value*))

(defonce *reducers-init-value* [])
(defonce reducers (atom []))

(defn reset-store! []
  (reset! store *store-init-value*))

(defn reset-reducers! []
  (reset! reducers *reducers-init-value*))

(defn defreducer
  "register reducer with name for debug purposes (or without it)"
  ([name reduce-fn]
   (swap! reducers conj {:name name
                         :fn reduce-fn}))
  ([reduce-fn]
   (defreducer nil reduce-fn)))

(defn dispatch-event [cursor event]
  (let [event-type (get event :type :unknown)]
    (doseq [r @reducers]
      (let [{name :name
             reduce-rn :fn} r]
        (swap! store reduce-rn event-type event cursor))))
  @store)
