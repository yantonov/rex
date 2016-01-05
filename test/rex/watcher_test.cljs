(ns rex.watcher-test
  (:require [rex.watcher :as sc]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn- setup! []
  (sc/reset-watchers!))

(deftest reset-watchers-test
  (do
    (setup!)
    (is (= [] (sc/get-watchers)))))

(deftest defsubscriver-test-with-meta
  (do
    (setup!)
    (sc/defwatcher (with-meta sc/empty-watcher {:meta-field :meta-value}))
    (let [watchers (sc/get-watchers)]
      (is (= 1 (count watchers)))
      (let [watcher (first watchers)]
        (is (= {:meta-field :meta-value} (:meta watcher)))
        (is (not (nil? (:fn watcher))))))))

(deftest defsubscriver-test-without-meta
  (do
    (setup!)
    (sc/defwatcher sc/empty-watcher)
    (let [watchers (sc/get-watchers)]
      (is (= 1 (count watchers)))
      (let [watcher (first watchers)]
        (is (nil? (:meta watcher)))
        (is (not (nil? (:fn watcher))))))))

