(ns rex.middleware-test
  (:require [rex.core :as sut]
            [rex.helpers :as h]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest reset-middlewares-test
  (do
    (sut/reset-middlewares!)
    (is (= [] @sut/middlewares))))

(deftest defmiddleware-with-name-test
  (do
    (sut/reset-middlewares!)
    (sut/defmiddleware :middleware1 h/id-middleware)
    (let [middlewares @sut/middlewares]
      (is (= 1 (count middlewares)))
      (let [m (first middlewares)]
        (is (= :middleware1 (:name m)))
        (is (not (nil? (:fn m))))))))

(deftest defmiddleware-without-name-test
  (do
    (sut/reset-middlewares!)
    (sut/defmiddleware h/id-middleware)
    (let [middlewares @sut/middlewares]
      (is (= 1 (count middlewares)))
      (let [m (first middlewares)]
        (is (nil? (:name m)))
        (is (not (nil? (:fn m))))))))

(deftest using-middlewares-test
  (do
    (sut/reset-middlewares!)
    (let [log-of-actions (atom [])]
      (sut/defmiddleware :log-action (fn [store next cursor action]
                                       (do
                                         (swap! log-of-actions conj action)
                                         (next cursor action))))
      (sut/dispatch nil (h/test-action-creator :some-event-type
                                               :some-value))
      (is (= [{:type :some-event-type
               :value :some-value}]
             @log-of-actions)))))
