(ns rex.ext.cursor
  (:require [rex.utils :as util]))

(defprotocol ICursor
  (cursor-key [this])

  (nest [this field])

  (featured [this field features])

  (zoom-in [this feature])

  ;; TODO:
  ;; get rid of update-state, get-state
  ;; store must provide explicit interface for reducers
  ;; for example with get-value(key), set-value(key,value) semantics
  (update-state [this state new-local-state])

  (get-state [this state]))

(deftype Cursor [items]
  ICursor
  (cursor-key [_] (map :field items))

  (nest [this field]
    (featured this field []))

  (featured [_ field features]
    (let [feature-list (if (nil? features)
                         []
                         features)]
      (Cursor. (conj items
                     {:field field
                      :features feature-list}))))

  (zoom-in [_ feature]
    (let [items (util/take-until-first
                 (fn [item]
                   (not (nil? (some #(= % feature)
                                    (:features item)))))
                 items)]
      (Cursor. items)))

  (update-state [this state new-local-state]
    (let [path (cursor-key this)]
      (if (empty? path)
        new-local-state
        (assoc-in state (cursor-key this) new-local-state))))

  (get-state [this state]
    (get-in state (cursor-key this))))

(defn make-cursor []
  (Cursor. []))
