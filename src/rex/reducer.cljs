(ns rex.reducer)

(defonce *reducers-init-value* [])
(defonce reducers (atom *reducers-init-value*))

(defn reset-reducers! []
  (reset! reducers *reducers-init-value*))

(defn get-reducers []
  @reducers)

(defn defreducer
  [reduce-fn]
  (swap! reducers conj {:name (get (meta reduce-fn) :name nil)
                        :fn reduce-fn}))

(defn id-reducer
  "trivial reducer, returns state as is"
  [store action]
  store)
