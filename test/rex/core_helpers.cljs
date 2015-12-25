(ns rex.core-helpers
  (:require [rex.core :as cr]
            [rex.reducer :as rd]
            [rex.middleware :as mw]
            [rex.watcher :as swt]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn reset-core! []
  (cr/reset-store!)
  (rd/reset-reducers!)
  (mw/reset-middlewares!)
  (swt/reset-watchers!))
