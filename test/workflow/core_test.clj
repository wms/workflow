(ns workflow.core-test
  (:require [workflow.core :as workflow]
            [clojurewerkz.neocons.rest.cypher :as cy])
  (:use clojure.test))

(defn connect [test]
  (workflow/connect)
  (test))

(defn 
  delete-all [test]
  (test)
  (cy/query "START n=node(*) MATCH n-[r?]-() DELETE n,r"))

(use-fixtures :each connect delete-all)

(deftest test-format
         (let [expected {:id 1234 :data {:type "object" :name "An Object"}}
               result (workflow/format expected)]
           (is
             (= (:id result) 1234))
           (is
             (= (:type result) "object"))
           (is
             (= (:name result) "An Object"))))

(deftest test-create-place
  (let [p (workflow/create-place {:name "Test Place"})]
    (is
      (= (:type (:data p)) "place"))))

(deftest test-create-transition
  (let [t (workflow/create-transition {:name "Test Transition"})]
    (is
      (= (:type (:data t)) "transition"))))

(deftest test-create-case
  (let [c (workflow/create-case {:name "Test Case"})]
    (is
      (= (:type (:data c)) "case"))))

(deftest test-transition-input
  (let [p1 (workflow/create-place {:name "Test Input Place"})
        tr (workflow/create-transition {:name "Test Transition"})
        ip (workflow/create-transition-input tr p1)]
    (is
      (= (:type ip) "input"))
    (is
      (= (:start ip) (:location-uri p1)))
    (is
      (= (:end ip) (:location-uri tr)))))

(deftest test-transition-output
  (let [p1 (workflow/create-place {:name "Test Output Place"})
        tr (workflow/create-transition {:name "Test Transition"})
        op (workflow/create-transition-output tr p1)]
    (is
      (= (:type op) "output"))
    (is
      (= (:start op) (:location-uri tr)))
    (is
      (= (:end op) (:location-uri p1)))))

(deftest test-case-occupy-place
  (let [p (workflow/create-place {:name "Test Place"})
        c (workflow/create-case {:name "Test Case"})
        r (workflow/case-occupy-place c p)]
    (is
      (= (:type r) "occupies"))
    (is
      (= (:start r) (:location-uri c)))
    (is
      (= (:end r) (:location-uri p)))))
