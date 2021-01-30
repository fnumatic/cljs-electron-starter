(ns elctr-starter.main.core
  (:require [cljs.nodejs :as nodejs]
            [elctr-starter.config :as cnf]
            [elctr-starter.main.elctr-helper :as ehlp :refer [on]]
            [os :as os]
            [electron :as electron :refer [app  crashReporter]]))

(def main-win :elctr/main-window)

(def config
  {:html-path       "index.html"
   :img-path        "img/png/electron.png"
   :title           "Electron Starter"})


(defn on-debug []
  (ehlp/open-dev-tools main-win))

(defn on-release []
  (ehlp/open-dev-tools main-win)
  (ehlp/set-application-menu nil))


(defn on-app-ready []
  (ehlp/create-window main-win config)
  (when js/goog.DEBUG (on-debug))
  (when (not js/goog.DEBUG) (on-release)))

(defn -main []
  (on nodejs/process "error" #(println "ERROR:" %))
  (on app :window-all-closed ehlp/quit-app)
  (on app :ready on-app-ready)
  (ehlp/start-crash-reporter {}))

(nodejs/enable-util-print!)

;;; "Linux" or "Darwin" or "Windows_NT"
(println (str "Start Electron App on " (.type os) "."))

(set! *main-cli-fn* -main)
