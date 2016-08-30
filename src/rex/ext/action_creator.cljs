(ns rex.ext.action-creator)

(defn action-creator-middleware
  "support of composite actions"
  [action get-store next-dispatch-fn]
  (letfn [(action-creator-dispatch-fn [action]
            (if (fn? action)
              (let [action-creator action]
                (do
                  (action-creator action-creator-dispatch-fn get-store)
                  (get-store)))
              (next-dispatch-fn action)))]
    (action-creator-dispatch-fn action)))
