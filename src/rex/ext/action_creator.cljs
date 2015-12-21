(ns rex.ext.action-creator)

(defn action-creator-middleware
  "support of composite actions"
  [action get-store next-dispatch-fn]
  (if (fn? action)
    (let [action-creator action]
      (action-creator next-dispatch-fn get-store))
    (next-dispatch-fn action)))
