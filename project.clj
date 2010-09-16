(defproject plugboard "1.5.0-SNAPSHOT"
  :description "A library to promote correct HTTP semantics for Clojure web applications built on Compojure"
  :url "http://github.com/malcolmsparks/plugboard"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]

  :dev-dependencies [
                     [swank-clojure "1.2.1"]
                     [hiccup "0.2.6"
                      :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]]
                     [ring/ring-core "0.2.5"
                      :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]]
                     [ring/ring-jetty-adapter "0.2.5"]
                     [compojure "0.4.1"
                      :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]
                      ]
                     [clj-http "0.1.1"]
;;                     [autodoc "0.7.1"]
                     ]
  )
