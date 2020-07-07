(ns shared.messaging-electron
  (:require [shared.messaging :as msg]
            [cljs.nodejs :as nodejs]))


(def Electron (nodejs/require "electron"))
(def dest (.-ipcMain Electron))

(def origin msg/origin)

(defn on [chn f]
  (msg/on dest chn f))

(defn send
    ([chn msg] (send dest chn msg))
    ([*dest chn msg] (msg/send *dest chn msg)))

(defn once [ chn f]
  (msg/once dest chn f))

(defn remove-listener [ chn f]
  (msg/remove-listener dest  chn f))

(defn remove-all [ chn]
  (msg/remove-all dest chn))