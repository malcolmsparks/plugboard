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

(ns plugboard.demos.links.webfunctions
  (:use
   plugboard.webfunction.context
   )
  (:require
   [plugboard.webfunction.webfunction :as web]
   [hiccup.core :as hiccup]
   )
  )

(defn ^{web/path "index.html"
        web/content-type "text/html"
        :title "Links"}
  index-html []
  (hiccup/html
   [:html
    [:head [:title (get-meta :title)]]
    [:body
     [:h1 (get-meta :title)]
     [:p
      "Here is a link to "
      [:a {:href (create-uri "other.html")} "another page"]
      "."
      ]
     ]]))

(defn ^{web/path "other.html"
        web/content-type "text/html"
        :title "Target link"}
  other-html []
  (hiccup/html
   [:html
    [:head [:title (get-meta :title)]]
    [:body
     [:h1 (get-meta :title)]
     [:p "You arrived safely at the correct page."]
     ]]))

