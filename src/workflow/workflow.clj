(ns workflow.workflow
  (:require [workflow.core :as core]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.records :as rec]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(defn
  ^{:doc "Fetch a workflow by id"}
  get-by-id
  [id]
  (let [node (nn/get id)]
    (if
      (= (:type (:data node) "workflow"))
           (core/format node)
           false
           )))

(defn
  ^{:doc "Create a workflow from a map which must include 'name' key'"}
  create
  [workflow]
  (let [node (nn/create (merge workflow {:type "workflow"}))]
    (core/format node)))

(defn
  ^{:doc "Create a place designated as the entry place for a Workflow"}
  create-entry-place
  [workflow place]
  (let [p (core/create-place place)]
    (cy/query "START w = node({wid}), p = node({pid}) CREATE UNIQUE w-[:entry]-p RETURN p"
              {:wid (:id workflow) :pid (:id p)})
    (core/format p)))
      
(defn
  ^{:doc "Fetch the entry place for a workflow"}
  get-entry-place
  [workflow]
  (let [{:keys [data]} (cy/query "START w = node({id}) MATCH w-[:entry]-p RETURN p LIMIT 1" {:id (:id workflow)})]
    ;; neocons seems to nest the result rather grauitously
    (core/format (first (map (comp rec/instantiate-node-from first) data)))))
