(ns rex.ext.cursor-test
  (:require [rex.ext.cursor :as sut]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest root-cursor-test
  (let [c (sut/make-cursor)]
    (is (= (sut/cursor-key c) []))))

(deftest nest-field-test
  (let [c (sut/make-cursor)
        n (sut/featured c "fieldname"  ["feature1" "feature2"])]
    (is (= (sut/cursor-key n) ["fieldname"]))))

(deftest zoom-in-found-feature-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "field1")
              (sut/featured "field2" ["feature2"]))]
    (is (= ["field1" "field2"]
           (sut/cursor-key (sut/zoom-in n "feature2"))))))

(deftest zoom-in-no-feature-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "field1")
              (sut/featured "field2" ["feature2"]))]
    (is (= ["field1" "field2"]
           (sut/cursor-key (sut/zoom-in n "unknown-feature"))))))

(deftest zoom-in-first-feature-occurence-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "field1")
              (sut/featured "field2" ["feature123"])
              (sut/nest "field3")
              (sut/featured "field4" ["feature123"]))]
    (is (= ["field1" "field2"]
           (sut/cursor-key (sut/zoom-in n "feature123"))))))

(deftest update-nested-state-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "a")
              (sut/nest "b"))
        m {"a" {"b" 1}}]
    (is (= {"a" {"b" 2}}
           (sut/update-state n m 2)))))

(deftest update-whole-state-test
  (let [c (sut/make-cursor)
        m {"a" {"b" 1}}]
    (is (= {"c" 123}
           (sut/update-state c m {"c" 123})))))

(deftest get-state-with-empty-cursor-test
  (let [c (sut/make-cursor)
        m {"a" {"b" 123}}]
    (is (= {"a" {"b" 123}}
           (sut/get-state c m)))))

(deftest get-local-state-test
  (let [c (sut/make-cursor)
        nested (sut/nest c "a")
        m {"a" {"b" 123}}]
    (is (= {"b" 123}
           (sut/get-state nested m)))))

(deftest get-local-state-with-invalid-cursor-test
  (let [c (sut/make-cursor)
        nested (sut/nest c "abracadabra")
        m {"a" {"b" 123}}]
    (is (nil? (sut/get-state nested m)))))

