(ns elctr-starter.init
  (:require
    [elctr-starter.core :as core]))

(enable-console-print!)

(defn start-front!
  []
  (core/init))

(start-front!)
