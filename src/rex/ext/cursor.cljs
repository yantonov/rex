(ns rex.ext.cursor
  (:require [rex.utils :as util]))

(defrecord CursorItem [field features])

(defn- add-features [cursor-item features]
  (CursorItem. (:field cursor-item)
               (concat (vec (:features cursor-item))
                       features)))

(defprotocol ICursor
  (is-empty [this])

  (cursor-key [this])

  (nest [this field])

  (parent [this])

  (featured [this features])

  (features [this])

  (zoom-in [this feature]))

(deftype Cursor [items]
  ICursor
  (is-empty [_]
    (empty? items))

  (cursor-key [_] (map :field items))

  (nest [this field]
    (Cursor. (conj items (CursorItem. field []))))

  (parent [this]
    (Cursor. (vec (butlast items))))

  (featured [this features]
    (if (is-empty this)
      this
      (let [last-item (last items)]
        (Cursor. (conj (vec (butlast items))
                       (add-features last-item features))))))

  (features [this]
    (if (is-empty this)
      []
      (:features (last items))))

  (zoom-in [_ feature]
    (let [items (util/take-until-first
                 (fn [item]
                   (not (nil? (some #(= % feature)
                                    (:features item)))))
                 items)]
      (Cursor. items))))

(defn make-cursor []
  (Cursor. []))

(defn set-state [this state new-local-state]
  (let [path (cursor-key this)]
    (if (empty? path)
      new-local-state
      (assoc-in state path new-local-state))))

(defn update-state [this state update-state-fn]
  (let [path (cursor-key this)]
    (if (empty? path)
      (update-state-fn state)
      (let [original-local-state (get-in state path)]
        (assoc-in state path (update-state-fn original-local-state))))))

(defn get-state [this state]
  (get-in state (cursor-key this)))
