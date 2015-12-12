(ns rex.subscriber)

(defonce *subscribers-init-value* [])
(defonce subscribers (atom *subscribers-init-value*))

(defn reset-subscribers! []
  (reset! subscribers *subscribers-init-value*))

(defn get-subscribers []
  @subscribers)

(defn empty-subscriber [store-value]
  ;; do somthing
  )

(defn defsubscriber
  ([cursor subscriber-callback]
   (swap! subscribers conj {:cursor cursor
                            :fn subscriber-callback})))
