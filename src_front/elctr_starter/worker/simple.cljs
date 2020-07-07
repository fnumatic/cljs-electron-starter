(ns elctr-starter.worker.simple
  (:require 
   [simple-workers.worker :as worker]
   ))


(defn parse [{:keys [rawtext] }]
  (identity rawtext))


(defn init
  []
  (worker/register :mirror  identity)
  (worker/register :parsetext parse)

  (worker/bootstrap))



(comment
  (+ 1 2)
  )