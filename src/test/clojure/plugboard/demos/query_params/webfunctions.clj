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

(ns plugboard.demos.query-params.webfunctions
  (:require
   [hiccup.core :as hiccup]
   [plugboard.webfunction.webfunction :as web]))

(defn ^{web/path "index.html"
        web/content-type "text/html"
        :title "Query parameters"}
  index-html []
  (hiccup/html
   [:html
    [:head [:title (web/get-meta :title)]]
    [:body
     [:h1 (web/get-meta :title)]
     (map (fn [coffee]
            (let [href (format "query.html?coffee=%s" coffee)]
              [:p [:a {:href href} href]]))
          ["Espresso" "Latte" "Cappuccino" "Americano" "Mocha"])
     [:p "This shows how multiple values can be queried as a vector :-"]
     (let [href (format "query.html?coffee=%s&coffee=%s" "Latte" "Espresso")]
       [:p [:a {:href href} href]])]]))

(defn article [s]
  (cond
   (some (set (take 1 s)) [\A \E \H \I \O \U]) "an"
   :otherwise "a"))

(defn ^{web/path "query.html"
        web/content-type "text/html"
        :title "Query parameters - result"}
  query-html []
  (let [coffee (web/get-query-param "coffee")]
    (hiccup.core/html
     [:html
      [:head [:title (web/get-meta :title)]]
      [:body
       [:h1 (format "You chose %s %s" (article coffee) coffee)]]])))
