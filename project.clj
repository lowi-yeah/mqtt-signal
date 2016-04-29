(defproject
  boot-project
  "0.0.0-SNAPSHOT"
  :dependencies
  [[org.danielsz/system "0.3.0-SNAPSHOT"]
   [environ "1.0.2"]
   [boot-environ "1.0.2"]
   [org.clojure/tools.nrepl "0.2.12"]
   [com.taoensso/timbre "4.3.1"]
   [clojurewerkz/machine_head "1.0.0-beta9"]
   [ring/ring-core "1.4.0"]
   [ring/ring-jetty-adapter "1.4.0"]
   [ring/ring-defaults "0.1.5"]
   [compojure "1.4.0"]]
  :source-paths
  ["src"])