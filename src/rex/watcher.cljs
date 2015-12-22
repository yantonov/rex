(ns rex.watcher)

(defonce *watchers-init-value* [])
(defonce watchers (atom *watchers-init-value*))

(defn reset-watchers! []
  (reset! watchers *watchers-init-value*))

(defn get-watchers []
  @watchers)

(defn empty-watcher [old-value new-value]
  ;; do something
  )

(defn defwatcher
  ([meta watcher-callback]
   (swap! watchers conj {:meta meta
                         :fn watcher-callback}))

  ([callback]
   (defwatcher nil callback)))
