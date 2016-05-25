(ns rex.ext.cursor-test
  (:require [rex.ext.cursor :as sut]
            [cljs.test :refer-macros [deftest testing is are]]))

(deftest root-cursor-test
  (let [c (sut/make-cursor)]
    (is (= (sut/cursor-key c) []))))

(deftest empty-cursor-test
  (let [c (sut/make-cursor)]
    (is (sut/is-empty c))))

(deftest nest-field-test
  (let [c (-> (sut/make-cursor)
              (sut/nest "fieldname"))]
    (is (= (sut/cursor-key c) ["fieldname"]))))

(deftest parent-test
  (let [c (-> (sut/make-cursor)
              (sut/nest "f1")
              (sut/nest "f2"))]
    (is (= (sut/cursor-key c)
           ["f1" "f2"]))

    (is (= (sut/cursor-key (-> c
                               (sut/parent)))
           ["f1"]))

    (is (= (sut/cursor-key (-> c
                               (sut/parent)
                               (sut/parent)))
           []))

    (is (= (sut/cursor-key (-> c
                               (sut/parent)
                               (sut/parent)
                               (sut/parent)))
           []))))

(deftest featured-test
  (let [c (-> (sut/make-cursor)
              (sut/nest "field")
              (sut/featured ["feature"]))]
    (is (= (sut/features c) ["feature"]))))

(deftest zoom-in-found-feature-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "field1")
              (sut/nest "field2")
              (sut/featured ["feature2"]))]
    (is (= ["field1" "field2"]
           (sut/cursor-key (sut/zoom-in n "feature2"))))))

(deftest zoom-in-no-feature-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "field1")
              (sut/nest "field2")
              (sut/featured ["feature2"]))]
    (is (= ["field1" "field2"]
           (sut/cursor-key (sut/zoom-in n "unknown-feature"))))))

(deftest zoom-in-first-feature-occurence-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "field1")
              (sut/nest "field2")
              (sut/featured ["feature123"])
              (sut/nest "field3")
              (sut/nest "field4")
              (sut/featured ["feature123"]))]
    (is (= ["field1" "field2"]
           (sut/cursor-key (sut/zoom-in n "feature123"))))))

(deftest set-nested-state-test
  (let [c (sut/make-cursor)
        n (-> c
              (sut/nest "a")
              (sut/nest "b"))
        m {"a" {"b" 1}}]
    (is (= {"a" {"b" 2}}
           (sut/set-state n m 2)))))

(deftest set-whole-state-test
  (let [c (sut/make-cursor)
        m {"a" {"b" 1}}]
    (is (= {"c" 123}
           (sut/set-state c m {"c" 123})))))

(deftest update-local-state-test
  (let [c (sut/make-cursor)
        nested (-> c
                   (sut/nest "a")
                   (sut/nest "b"))
        m {"a" {"b" 123}}]
    (is (= {"a" {"b" 124}}
           (sut/update-state nested m inc)))))

(deftest update-global-state-test
  (let [c (sut/make-cursor)
        m {"a" {"b" 123}}]
    (is (= {"a" {"b" 124}}
           (sut/update-state c m (fn [s] (assoc-in s ["a" "b"] 124)))))))

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

