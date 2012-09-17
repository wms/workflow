(ns workflow.workflow-test
  (:require [workflow.workflow :as workflow]
            [workflow.core :as core]
            [clojurewerkz.neocons.rest.cypher :as cy])
  (:use clojure.test))

(defn connect [test]
  (core/connect)
  (test))

(defn 
  delete-all [test]
  (test)
  (cy/query "START r = rel(*) DELETE r")
  (cy/query "START n = node(*) DELETE n"))

(use-fixtures :each connect delete-all)

(deftest test-create
         (let [w (workflow/create {:name "Test Workflow"})]
                                  (is
                                    (= (:type w) "workflow"))))

(deftest test-get-by-id
         (let [w1 (workflow/create {:name "Test Workflow"})
               w2 (workflow/get-by-id (:id w1))]
           (is
             (= (:type w2) "workflow"))
           (is
             (= (:name w2) "Test Workflow"))))

(deftest test-find
         (let [w1 (workflow/create {:name "Test Workflow 1"})
               w2 (workflow/create {:name "Test Workflow 2"})
               w  (workflow/find)]
           (is
             (= (count w) 2))))

(deftest test-create-entry-place
         (let [w (workflow/create {:name "Test Workflow"})
               p (workflow/create-entry-place w {:name "Start Place"})]
           (is
             (= (:type p) "place"))
           (is
             (= (:name p) "Start Place"))))

(deftest test-get-entry-place
         (let [w (workflow/create {:name "Test Workflow"})
               p1 (workflow/create-entry-place w {:name "Start Place"})
               p2 (workflow/get-entry-place w)]
           (is
             (= (:id p1) (:id p2)))))
