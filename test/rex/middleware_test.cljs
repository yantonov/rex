(ns rex.middleware-test
  (:require [rex.middleware :as mw]
            [cljs.test :refer-macros [deftest testing is are]]))

(defn- setup! []
  (mw/reset-middlewares!))

(deftest reset-middlewares-test
  (do
    (setup!)
    (is (= [] (mw/get-middlewares)))))

(deftest defmiddleware-with-name-test
  (do
    (setup!)
    (mw/defmiddleware (with-meta mw/id-middleware
                        {:name :middleware1}))
    (let [middlewares (mw/get-middlewares)]
      (is (= 1 (count middlewares)))
      (let [m (first middlewares)]
        (is (= :middleware1 (:name m)))
        (is (not (nil? (:fn m))))))))

(deftest defmiddleware-without-name-test
  (do
    (setup!)
    (mw/defmiddleware mw/id-middleware)
    (let [middlewares (mw/get-middlewares)]
      (is (= 1 (count middlewares)))
      (let [m (first middlewares)]
        (is (nil? (:name m)))
        (is (not (nil? (:fn m))))))))
