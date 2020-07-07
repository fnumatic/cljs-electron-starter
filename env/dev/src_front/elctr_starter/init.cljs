(ns  elctr-starter.init
  (:require
    [elctr-starter.core :as core]))

(enable-console-print!)

(defn start-front!
  []
  (println :reload-app)
  (core/mount-root))

(defonce sf
  
  (do (println :loading-loading)
      (core/init)))