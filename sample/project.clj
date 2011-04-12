(defproject sample "1.0"
  :description "A sample project using plugboard"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [plugboard "1.8.2"]
                 [compojure "0.6.0"]
                 [ring/ring-core "0.3.5"]
                 [ring/ring-jetty-adapter "0.3.5"]
                 [clout "0.4.0"]]
  :license {:name "AGPLv3"}

  :dev-dependencies [
                     [swank-clojure "1.2.1"]])
