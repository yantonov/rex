(ns rex.subscriber-test
  (:require [rex.subscriber :as sc]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn- setup! []
  (sc/reset-subscribers!))

(deftest reset-subscribers-test
  (do
    (setup!)
    (is (= [] (sc/get-subscribers)))))

(deftest defsubscriver-test-with-meta
  (do
    (setup!)
    (sc/defsubscriber :some-meta sc/empty-subscriber)
    (let [subscribers (sc/get-subscribers)]
      (is (= 1 (count subscribers)))
      (let [subscriber (first subscribers)]
        (is (= :some-meta (:meta subscriber)))
        (is (not (nil? (:fn subscriber))))))))

(deftest defsubscriver-test-without-meta
  (do
    (setup!)
    (sc/defsubscriber sc/empty-subscriber)
    (let [subscribers (sc/get-subscribers)]
      (is (= 1 (count subscribers)))
      (let [subscriber (first subscribers)]
        (is (nil? (:meta subscriber)))
        (is (not (nil? (:fn subscriber))))))))

