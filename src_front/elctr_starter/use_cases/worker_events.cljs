(ns elctr-starter.use-cases.worker-events
  (:require [re-frame.core :as r]
            [simple-workers.core :as main]
            [worker-fx.core]))

(r/reg-event-fx
 :on-worker-fx-success
 (fn [_ [_ result]]
   (println "success" result)))

(r/reg-event-fx
 :on-worker-fx-error
 (fn [_ [_ result]]
   (println "error" result)))

(r/reg-event-fx
 :worker-fx
 (fn [coeffects [_ task]]
   (let [worker           (-> coeffects :db :worker)
         task-with-worker (assoc task :worker worker)]

     {:worker task-with-worker})))

(r/reg-event-fx
 :initialize-worker
 (fn [cofx _]

   (let [w (main/create-one   "js/worker.js")         ]
     {:db (-> (:db cofx)
              (assoc  :worker w))})))

  ;