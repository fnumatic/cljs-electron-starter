(ns elctr-starter.use-cases.all
  (:require [re-frame.core :as r]
            [elctr-starter.db :as db]
            [elctr-starter.use-cases.worker-events]
            [elctr-starter.use-cases.utils :refer [gdb sdb]]))
            
  
;; Queries-----------------
(def selected-view-path [:ui :selected-view])
(def msg-path [:ui :lastmsg])
(def show-info-path [:ui :show-info])

(r/reg-event-fx
  :app/boot
    (fn [cofx [_ settings]]
      (let [default-view (keyword (:defaultview settings))   ]
        {
         :dispatch-n [
                      (when default-view [:ui/change-view default-view])
                      
                      ]})))

(defn time-stamp []
  (.toLocaleTimeString (js/Date.)))

(r/reg-event-fx
 :ui/change-viewL
 (fn [_ [_ view]]
   {:dispatch-n [[:ui/change-view view]
                 [:msg/log {:msg :ui/change-view
                            :time (time-stamp)}]
                 ]}))

(r/reg-sub :ui/lastmsg (gdb msg-path))
(r/reg-event-db :msg/log (sdb msg-path))

(r/reg-sub :ui/show-info (gdb show-info-path))
(r/reg-event-db :ui/show-info (sdb show-info-path))

(r/reg-sub :ui/selected-view (gdb selected-view-path))
(r/reg-event-db :ui/change-view (sdb selected-view-path))

(r/reg-event-db :app/initialize-db (constantly db/default-db))
