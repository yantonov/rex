(ns rex.subscriber)

(defonce *subscribers-init-value* [])
(defonce subscribers (atom *subscribers-init-value*))

(defn reset-subscribers! []
  (reset! subscribers *subscribers-init-value*))

(defn get-subscribers []
  @subscribers)

(defn empty-subscriber [old-value new-value]
  ;; do something
  )

(defn defsubscriber
  ([meta subscriber-callback]
   (swap! subscribers conj {:meta meta
                            :fn subscriber-callback}))

  ([callback]
   (defsubscriber nil callback)))
