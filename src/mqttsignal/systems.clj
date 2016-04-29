(ns mqttsignal.systems
    (:require [system.core :refer [defsystem]]
      (system.components
        [repl-server :refer [new-repl-server]])
      [environ.core :refer [env]]
      [mqttsignal.config :as config]
      [mqttsignal.mqtt :refer [new-mqtt]]))

(defsystem dev-system
           [:mqtt (new-mqtt (config/mqtt-broker) (config/mqtt-id))])

(defsystem prod-system
           [:mqtt (new-mqtt (config/mqtt-broker) (config/mqtt-id))
            :repl-server (new-repl-server (Integer. (env :repl-port)))])
