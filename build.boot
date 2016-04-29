(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [org.danielsz/system "0.3.0-SNAPSHOT"]
                 [environ "1.0.2"]
                 [boot-environ "1.0.2"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [com.taoensso/timbre "4.3.1"]
                 [clojurewerkz/machine_head "1.0.0-beta9"]
                 [thi.ng/math "0.2.1"]
                 ])

(require
 '[reloaded.repl :as repl :refer [start stop go reset]]
 '[mqttsignal.systems :refer [dev-system]]
 '[environ.boot :refer [environ]]
 '[system.boot :refer [system run]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:mqtt-id "mqtt-sine"
                  :mqtt-broker "tcp://127.0.0.1:1883"})
   (watch :verbose true)
   (system :sys #'dev-system :auto true :files ["mqtt.clj"])
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:mqtt-id "mqttkafkabridge"
                  :mqtt-broker "tcp://127.0.0.1:1883"})
   (run :main-namespace "mqttsignal.core" :arguments [#'dev-system])
   (wait)))

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot :namespace '#{mqttsignal.core})
   (pom :project 'mqttsignal
        :version "1.0.0")
   (uber)
   (jar :main 'mqttsignal.core)))



;//   _     _           _            _
;//  | |___(_)_ _    ___ |_  __ _ __| |_____ __ __
;//  | / -_) | ' \  (_-< ' \/ _` / _` / _ \ V  V /
;//  |_\___|_|_||_| /__/_||_\__,_\__,_\___/\_/\_/
;//
;Cursive requires a project.clj file to infer some important information.
;The lein-generate task generates a project.clj file from this boot file so Cursive knows what's what.
(defn- generate-lein-project-file! [& {:keys [keep-project] :or {:keep-project true}}]
       (require 'clojure.java.io)
       (let [pfile ((resolve 'clojure.java.io/file) "project.clj")
             ; Only works when pom options are set using task-options!
             {:keys [project version]} (:task-options (meta #'boot.task.built-in/pom))
             prop #(when-let [x (get-env %2)] [%1 x])
             head (list* 'defproject (or project 'boot-project) (or version "0.0.0-SNAPSHOT")
                         (concat
                           (prop :url :url)
                           (prop :license :license)
                           (prop :description :description)
                           [:dependencies (get-env :dependencies)
                            :source-paths (vec (concat (get-env :source-paths)
                                                       (get-env :resource-paths)))]))
             proj (pp-str head)]
            (if-not keep-project (.deleteOnExit pfile))
            (spit pfile proj)))

(deftask make-lein
         "Generate a leiningen `project.clj` file.
          This task generates a leiningen `project.clj` file based on the boot
          environment configuration, including project name and version (generated
          if not present), dependencies, and source paths. Additional keys may be added
          to the generated `project.clj` file by specifying a `:lein` key in the boot
          environment whose value is a map of keys-value pairs to add to `project.clj`."
         []
         (generate-lein-project-file! :keep-project true))
