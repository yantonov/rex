(ns rex.helpers)

(defn create-event
  "simple event with type and value"
  [type value]
  {:type type
   :value value})

(def test-action-creator
  "simple action creator, dispatch signgle event with type and value"
  (fn [type value]
    (fn [dispatch cursor state]
      (create-event type value))))

(defn id-middleware
  "trivial middleware, just delegate to next dispatch function"
  [store next-dispatch-fn cursor action]
  (next-dispatch-fn cursor action))
