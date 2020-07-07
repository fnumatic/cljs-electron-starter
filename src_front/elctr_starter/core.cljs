(ns  elctr-starter.core
  (:require 
            [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [elctr-starter.use-cases.all]
            [elctr-starter.views.main-view :as views]
            [elctr-starter.config :as config]
            [re-pressed.core :as rp]
            [elctr-starter.use-cases.keyboard :as kbrd]))


(defn dev-setup []
  (when ^boolean config/debug?
    (enable-console-print!)
    (tap> "hello shadow")))

(defn mount-root []
  (rdom/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn set-title [txt]
  (set! (.-title js/document) txt))

(defn ^:export init []
  (re-frame/dispatch-sync [:app/initialize-db])
  (re-frame/dispatch [::rp/add-keyboard-event-listener "keydown"])
  (re-frame/dispatch [:initialize-worker])
  (re-frame/dispatch kbrd/shortcuts)
  (dev-setup)
  (mount-root)
  (set-title "Electron Starter")
  )


