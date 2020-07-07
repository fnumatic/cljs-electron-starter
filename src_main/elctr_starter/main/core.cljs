(ns elctr-starter.main.core
    (:require [cljs.nodejs :as nodejs]
              [elctr-starter.config :as cnf]
              ))

(def win (atom nil))

(def path (nodejs/require "path"))
(def url (nodejs/require "url"))
(def electron (nodejs/require "electron"))
(def app (or (.-app electron) (.-app (.-remote electron))))


(defn web-contents [] (.-webContents @win))

(def browser-window (.-BrowserWindow electron))

(def crash-reporter (.-crashReporter electron))

(def menu (.-Menu electron))

(def Os (nodejs/require "os"))

(defonce window win)

(defn on-window-closed
  []
  (reset! window nil))

(defn on-window-ready-to-show
  []
  (.show @window))


(defn on-win-close []
  (fn [] (reset! win nil)))


(defn create-window []
  (let [_ (println cnf/config)
        ;p (if (= :prod (:env cnf/config)) "/../" "/../../../../")
        p (if (= :prod (:env cnf/config)) "/../" "/../")
        u2 (->> "index.html"  (.join path  js/__dirname p) (.resolve path))
        icn (.join path js/__dirname p "img/png/electron.png")
        _ (println :imagepath icn)
        env (:env cnf/config)]

    (reset! win (browser-window. (clj->js {:width 800 :height 600
                                           :title "Electron Starter"
                                           :icon icn
                                           :webPreferences {:nodeIntegration true}})))



    (println env)
    (.loadFile ^js/electron.BrowserWindow @win u2)
    (when js/goog.DEBUG
      ( do 
       (.openDevTools (.-webContents @win))
       ))
    
    (when (not js/goog.DEBUG) 
      (do
        (.openDevTools (.-webContents @win))
        (.setApplicationMenu menu nil)))
    
    (.on ^js/electron.BrowserWindow @win  "closed" on-win-close)))

(defn log [m]
  (.log js/console m))



(defn on-app-ready []

  (create-window)

  )


(defn on-app-quit
  [e exit-code]
  (println "Main process quitting. Exit code:" exit-code))

(defn on-app-window-all-closed
  []
  (if (not= (.-platform nodejs/process) "darwin")
      (.quit app)))

(defn on-process-error
  [error]
  (println "ERROR:" error))

(defn -main []
  (println "Main process started, app is starting up.")
  (.on nodejs/process "error" on-process-error)
  (.on app "window-all-closed" on-app-window-all-closed)
  (.on app "quit" on-app-quit)
  (.on app "ready" on-app-ready)
  (.start crash-reporter (clj->js {:companyName "Your Company NName"
                                   :submitURL   "http://example.com/"})))

(nodejs/enable-util-print!)

;;; "Linux" or "Darwin" or "Windows_NT"
(.log js/console (str "Start classregister on " (.type Os) "."))

(set! *main-cli-fn* -main)
