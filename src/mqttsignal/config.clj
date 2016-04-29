(ns mqttsignal.config
    (:require [environ.core :refer [env]]))

(defn- get-env [key default]
       (let [val (get env key)]
            (if val val default)))

(defn mqtt-broker []
      (get-env :mqtt-broker "tcp://mosquitto1:1883"))

(defn mqtt-id []
      (get-env :mqtt-id "mqtt-signal"))
