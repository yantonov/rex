(ns rex.reducer)

(defonce *reducers-init-value* [])
(defonce reducers (atom *reducers-init-value*))

(defn reset-reducers! []
  (reset! reducers *reducers-init-value*))

(defn get-reducers []
  @reducers)

(defn defreducer
  "register reducer with name (for debug purposes) or without it"
  ([name reduce-fn]
   (swap! reducers conj {:name name
                         :fn reduce-fn}))
  ([reduce-fn]
   (defreducer nil reduce-fn)))

(defn id-reducer
  "trivial reducer, returns state as is"
  [store event-type event cursor]
  store)
