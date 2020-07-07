(ns elctr-starter.use-cases.keyboard
  (:require 
   [re-pressed.core :as rp]
   ))

(def shortcuts
  [::rp/set-keydown-rules
   {;; takes a collection of events followed by key combos that can trigger the event
    :event-keys [;; Event & key combos 1
                 
                 
                 [[:ui/save-text]
                  [{:keyCode 83
                    :ctrlKey true
                    :shiftKey true}]]]}])
