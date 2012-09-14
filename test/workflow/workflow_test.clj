(ns workflow.workflow-test
  (:require [workflow.workflow :as workflow])
  (:use clojure.test))

(deftest create
         (let [w (workflow/create {:name "Test Workflow"})]
                                  (is
                                    (= (:type w) "workflow"))))

(deftest get-by-id
         (let [w1 (workflow/create {:name "Test Workflow"})
               w2 (workflow/get-by-id (:id w1))]
           (is
             (= (:type w2) "workflow"))
           (is
             (= (:name w2) "Test Workflow"))))

(deftest find
         (let [w1 (workflow/create {:name "Test Workflow 1"})
               w2 (workflow/create {:name "Test Workflow 2"})
               w  (workflow/find)]
           (is
             (= (count w) 2))))

(deftest create-entry-place
         (let [w (workflow/create {:name "Test Workflow"})
               p (workflow/create-entry-place w {:name "Start Place"})]
           (is
             (= (:type p) "place"))
           (is
             (= (:name p) "Start Place"))))

(deftest get-entry-place
         (let [w (workflow/create {:name "Test Workflow"})
               p1 (workflow/create-entry-place w {:name "Start Place"})
               p2 (workflow/get-entry-place w)]
           (is
             (= (:id p1) (:id p2)))))
