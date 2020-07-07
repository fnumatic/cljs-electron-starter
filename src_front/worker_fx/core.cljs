(ns worker-fx.core
  (:require [cljs.core.async :refer [<!]]
            [re-frame.core :refer [reg-fx dispatch]]
            [simple-workers.core :refer [do-with-worker!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(reg-fx
 :worker
 (fn worker-fx
   [{:keys [worker on-success on-error] :as data}]
   (go
     (let [result-chan
           (do-with-worker! worker data)

           {:keys [state] :as result}
           (<! result-chan)]

       (if (= :success (keyword state))
         (dispatch (conj on-success result))
         (dispatch (conj on-error result)))))))