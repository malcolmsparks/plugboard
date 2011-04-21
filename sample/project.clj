(defproject sample "1.0"
  :description "A sample project using plugboard"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [plugboard "1.9.0"]
                 [compojure "0.6.2"]
                 [ring/ring-core "0.3.7"]
                 [ring/ring-jetty-adapter "0.3.7"]
                 [clout "0.4.1"]]
  :license {:name "AGPLv3"}
  :dev-dependencies [
                     [swank-clojure "1.2.1"]])
