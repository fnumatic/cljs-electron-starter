(ns elctr-starter.main.elctr-helper
  (:require 
   [cljs.nodejs :as nodejs]
   [electron :refer [app BrowserWindow crashReporter Menu]]
   [path :as path]))

(def electron-db (atom {}))

(defn on [what k func]
  (.on ^js what (name k) func))

(defn win [k]
  (get @electron-db k))

(defn show [k]
  (.show (win k)))

(defn close [k]
  (swap! electron-db dissoc k))

(defn load [k path]
  (.loadFile ^js/electron.BrowserWindow (win k) path))

(def prefix "/../")

(defn path* [ file]
  (->> file
       (.join path  js/__dirname prefix)))

(defn html-path* [ file]
  (->> (path* file)
       (.resolve path)))

(defn create-window* [k config]
  (->>
   (BrowserWindow. (clj->js config))
   (swap! electron-db assoc k)))

(defn create-window [k {:keys [html-path img-path ] :as config}]
  (create-window*
   k
   (merge
    {:width           800
     :height          600
     :title           "TITLE"
     :icon            (path*  img-path)
     :backgroundColor "#fff"
     :webPreferences  {:contextIsolation   false
                       :nodeIntegration    true
                       :enableRemoteModule true}}))

  (load k (html-path* html-path))

  (on (win k) :closed #(close k)))

(defn web-contents [k] 
  ^js/electron.WebContents (.-webContents (win k)))

(defn set-application-menu [menu]
  (.setApplicationMenu Menu menu))

(defn open-dev-tools [k]
  (.openDevTools (web-contents k)))

(defn start-crash-reporter [config]
  (.start crashReporter (clj->js (merge 
                                  {:companyName "Your Company NName"
                                   :submitURL   "http://example.com/"}
                                  config))))

(defn quit-app
  []
  (when (not= (.-platform nodejs/process) "darwin")
    (.quit app)))