(ns rex.subscriber-test
  (:require [rex.subscriber :as sc]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn- setup! []
  (sc/reset-subscribers!))

(deftest reset-subscribers-test
  (do
    (setup!)
    (is (= [] (sc/get-subscribers)))))

(deftest defsubscriver-test
  (do
    (setup!)
    (sc/defsubscriber :some-cursor-value sc/empty-subscriber)
    (let [subscribers (sc/get-subscribers)]
      (is (= 1 (count subscribers)))
      (let [subscriber (first subscribers)]
        (is (= :some-cursor-value (:cursor subscriber)))
        (is (not (nil? (:fn subscriber))))))))
