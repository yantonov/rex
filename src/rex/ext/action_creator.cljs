(ns rex.ext.action-creator)

(defn action-creator-middleware
  "support of composite actions"
  [action get-store next-dispatch-fn]
  (letfn [(action-creator-dispatch-fn [action]
            (if (fn? action)
              (do
                (action)
                (get-store))
              (next-dispatch-fn action)))]
    (action-creator-dispatch-fn action)))
