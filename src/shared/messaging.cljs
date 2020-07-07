(ns shared.messaging
  (:require [cljs.reader :as rdr :refer [read-string]]))

;parse: cljs.reader/read-string
;stringify: prn-str

(defn send [dst chn msg]
  ;(println "send: " msg)
  (.send dst (clj->js chn) (prn-str msg)))

(defn fx [f evnt param]
  ;(println "receive: " evnt (read-string param))
  (f evnt (read-string param)))

(defn on [src chn f]
  (println "register fn on" chn)
  (.on src (clj->js chn) (partial fx f)))

(defn once [src chn f]
  (.once src (clj->js chn) (partial fx f)))

(defn remove-listener [src chn f]
  (.removeListener src (clj->js chn)  f))

(defn remove-all [src chn]
  (.removeAllListeners src (clj->js chn)))

(defn origin [event]
 (.-sender event))