(ns mqttsignal.mqtt
    (:require
      [com.stuartsierra.component :as component]
      [taoensso.timbre :as log]
      [clojure.core.async :refer [go-loop <! >!! chan timeout alts!]]
      [clojurewerkz.machine-head.client :as mh]
      [thi.ng.math.core :as m]
      [mqttsignal.config :as config]
      ))

(def increment 0.01)
(def interval 200)
(def topic "sine/sine-1/e")

(defn- map* [x]
       (int (m/map-interval x -1.0 1.0 0.0 255.0)))

(defn- send-message [conn x]
       (mh/publish conn topic (str (map* (Math/sin x)))))

(defn- run
       [f time-in-ms]
       (let [stop (chan)]
            (go-loop [x (* -1 m/HALF_PI)]
                     (let [timeout-ch (timeout time-in-ms)
                           [v ch] (alts! [timeout-ch stop])]
                          (if (= ch stop)
                            (log/info "stopping go-loop.")
                            (do
                              (f x)
                              (recur (+ increment x))))))
            stop))

(defrecord Mqtt [mqtt-broker mqtt-id conn stop-chan]
           component/Lifecycle
           (start [component]
                  (let [conn (mh/connect mqtt-broker mqtt-broker)
                        stop-channel (run (partial send-message conn) interval)]
                       (log/info (str "MQTT (" mqtt-id "@" mqtt-broker ")"))


                       (assoc component :conn conn :stop-chan stop-channel)))
           (stop [component]
                 (if (mh/connected? conn) (mh/disconnect conn))
                 (>!! stop-chan :stop)
                 (log/info (str "stopping component: MQTT"))
                 (dissoc component :conn :stop-chan)))

(defn new-mqtt [mqtt-broker mqtt-id]
      (map->Mqtt {:mqtt-broker mqtt-broker :mqtt-id mqtt-id}))