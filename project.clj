;; Copyright 2010 Malcolm Sparks.
;;
;; This file is part of Plugboard.
;;
;; Plugboard is free software: you can redistribute it and/or modify it under the
;; terms of the GNU Affero General Public License as published by the Free
;; Software Foundation, either version 3 of the License, or (at your option) any
;; later version.
;;
;; Plugboard is distributed in the hope that it will be useful but WITHOUT ANY
;; WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
;; A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
;; details.
;;
;; Please see the LICENSE file for a copy of the GNU Affero General Public License.

(defproject plugboard "1.6.0"
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
                     [clout "0.2.0"
                      :exclusions
                      [org.clojure/clojure org.clojure/clojure-contrib]
                      ]
                     [clj-http "0.1.1"]
                     [autodoc "0.7.1"]
                     ]
  )
