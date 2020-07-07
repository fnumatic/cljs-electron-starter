(ns elctr-starter.config)

(def debug?
  ^boolean js/goog.DEBUG)


(def config
  {:env            :prod
   :browser-window {:width  1024
                    :height 768}})
