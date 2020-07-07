(ns elctr-starter.main.init
  (:require
    [cljs.nodejs :as nodejs]
    [elctr-starter.main.core :as core]))

(nodejs/enable-util-print!)


(def start-electron!
  (fn []
    (reset! core/window nil)
    (core/-main)))

(set! *main-cli-fn* start-electron!)

