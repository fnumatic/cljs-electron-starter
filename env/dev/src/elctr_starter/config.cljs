(ns elctr-starter.config)

(def debug?
  ^boolean js/goog.DEBUG)

(def config
  {:env            :dev
   :dev-tools      {:auto-open? false
                    :position   "bottom"}
   :browser-window {:width  1024
                    :height 768}})
