(ns rex.middleware-test
  (:require [rex.core :as cr]
            [rex.middleware :as mw]
            [rex.helpers :as hp]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-middlewares-test
  (do
    (mw/reset-middlewares!)
    (is (= [] @cr/middlewares))))

(deftest defmiddleware-with-name-test
  (do
    (mw/reset-middlewares!)
    (mw/defmiddleware :middleware1 mw/id-middleware)
    (let [middlewares @cr/middlewares]
      (is (= 1 (count middlewares)))
      (let [m (first middlewares)]
        (is (= :middleware1 (:name m)))
        (is (not (nil? (:fn m))))))))

(deftest defmiddleware-without-name-test
  (do
    (mw/reset-middlewares!)
    (mw/defmiddleware mw/id-middleware)
    (let [middlewares @cr/middlewares]
      (is (= 1 (count middlewares)))
      (let [m (first middlewares)]
        (is (nil? (:name m)))
        (is (not (nil? (:fn m))))))))

(deftest using-middlewares-test
  (do
    (mw/reset-middlewares!)
    (let [log-of-actions (atom [])]
      (mw/defmiddleware :log-action (fn [store next cursor action]
                                      (do
                                        (swap! log-of-actions conj action)
                                        (next cursor action))))
      (cr/dispatch nil (hp/test-action-creator :some-event-type
                                              :some-value))
      (is (= [{:type :some-event-type
               :value :some-value}]
             @log-of-actions)))))
