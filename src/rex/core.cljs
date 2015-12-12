(ns rex.core
  (:require [rex.reducer :as rc]
            [rex.middleware :as mw]
            [rex.dispatch :as dpt]))

(defonce *store-init-value* {})
(defonce store (atom *store-init-value*))

(defn reset-store! []
  (reset! store *store-init-value*))

(defn- get-store []
  @store)

(defn- update-store [fn]
  (swap! store fn))

(defn dispatch-event [cursor event]
  (dpt/dispatch-event get-store
                      update-store
                      rc/get-reducers
                      mw/get-middlewares
                      cursor
                      event))

(defn dispatch [cursor action-creator]
  (dpt/dispatch get-store
                update-store
                rc/get-reducers
                mw/get-middlewares
                cursor
                action-creator))
