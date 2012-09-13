(ns workflow.core
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.records :as rec]))

(defn connect
  [] (nr/connect! "http://localhost:7474/db/data/"))

(defn format
  [data]
  (merge (:data data) {:id (:id data)}))

(defn create-place
  [place]
  (nn/create (merge place {:type "place"})))

(defn create-transition
  [transition]
  (nn/create (merge transition {:type "transition"})))

(defn create-case
  [case]
  (nn/create (merge case {:type "case"})))

(defn create-transition-input
  [transition place]
  (nrl/create place transition :input))

(defn create-transition-output
  [transition place]
  (nrl/create transition place :output))

(defn case-occupy-place
  [case place]
  (nrl/create case place :occupies))

(defn case-meets-conditions
  [case transition]
  (def conditions (:conditions (:data transition)))
  (declare ^:dynamic data)
  (every? (fn[condition]
    (binding [data (:data case)]
    (eval (read-string condition)))
  ) conditions))

(defn get-transition-input-cases
  [transition]
  (let [{:keys [data]} (cy/query "START trans = node({id}) MATCH trans-[:input]-place-[:occupies]-case RETURN case" {:id (:id transition)})]
    (map (comp rec/instantiate-node-from first) data)))

(defn get-transition-output-places
  [transition]
  (let [{:keys [data]} (cy/query "START trans = node({id}) MATCH trans-[:output]-place RETURN place" {:id (:id transition)})]
    (map (comp rec/instantiate-node-from first) data)))

(defn consume-transition-input
  [transition case]
  ;; delete occupies relation between case and transition input places
  (cy/query
    "START trans = node({trans}), case = node({case}) MATCH trans-[:input]-place-[r:occupies]-case DELETE r"
    {:trans (:id transition) :case (:id case)}))

(defn produce-transition-output
  [transition case]
  ;; create occupies relation between case and transition output places
  (def outputs (get-transition-output-places transition))
  (doseq [output outputs] (case-occupy-place case output)))
  
(defn fire-transition
  [transition]

  ;; get all cases occupying input places
  (def cases
    (get-transition-input-cases transition))

  ;; consume and produce tokens for cases that meet conditions
  (doseq [case (filter (fn [case]
    (case-meets-conditions case transition)) cases)]
      (consume-transition-input transition case)
      (produce-transition-output transition case)))
