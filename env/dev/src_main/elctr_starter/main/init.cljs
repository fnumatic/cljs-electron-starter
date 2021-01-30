(ns elctr-starter.main.init
  (:require
    [cljs.nodejs :as nodejs]
    [elctr-starter.main.core :as core]
    [elctr-starter.main.elctr-helper :refer [close]]))

(nodejs/enable-util-print!)


(def start-electron!
  (fn []
    (close core/main-win)
    (core/-main)))

(set! *main-cli-fn* start-electron!)

