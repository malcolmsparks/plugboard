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

(ns plugboard.demos.status-views.webfunctions
  (:use clojure.contrib.prxml)
  (:require
   [plugboard.webfunction.webfunction :as web]
   )
  )

(defn ^{web/path "index.html"
        web/content-type "text/html"
        :title "Index"}
  index-html []
  (with-out-str
    (prxml
     [:h1 (web/get-meta :title)]
     [:p "Click on this " [:a {:href "missing.html"} "missing link"] "."]
     )))

(defn ^{web/resource false
        web/status 404
        web/content-type "text/html"
        :title "Not found!"}
  not-found-html []
  (with-out-str
    (prxml
     [:body
      [:h1 (web/get-meta :title)]
      [:p "Oh dear. Your page couldn't be found."]]))) ; TODO: Indicate page in response.
