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

(ns plugboard.demos.menu
  (:use clojure.contrib.prxml))

(defn render-page []
  (with-out-str
    (prxml
     [:h1 "Plugboard"]
     [:h2 "Main menu"]
     [:ul
      [:li [:a {:href "hello-world/"} "hello-world"]]
      [:li [:a {:href "query-params/"} "query-params"]]
      [:li [:a {:href "links/"} "links"]]
      [:li [:a {:href "forms/"} "forms"]]
      [:li [:a {:href "status-views/"} "status-views"]]
      [:li [:a {:href "basic-auth/"} "basic-auth"]]
      [:li [:a {:href "custom-auth/index.html"} "custom-auth"]]
      [:li [:a {:href "accept/index"} "accept"]]
      ])))
     
