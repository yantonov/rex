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
