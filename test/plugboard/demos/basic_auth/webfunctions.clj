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

(ns plugboard.demos.basic-auth.webfunctions
  (:use
   plugboard.webfunction.context
   )
  (:require
   [plugboard.webfunction.webfunction :as web]
   [hiccup.core :as hiccup]
   )
  )

(defn ^{web/uri "/basic-auth/index.html"
        web/content-type "text/html"
        :title "Basic-Auth demo"}
  index-html []
  (hiccup/html
   [:h1 (get-meta :title)]
   [:p [:a {:href "secret-place.html"} "Click here"]
    " - the user id is 'scott', the password is 'tiger'."]
   ))

(defn ^{web/uri "/basic-auth/secret-place.html"
        web/content-type "text/html"
        :title "Secret place"}
  secret-place-html []
  (hiccup/html
   [:p "Congratulations, you have passed into the secret place."]
   ))

(defn ^{web/uri "/basic-auth/secret-place.html"
        web/content-type "text/html"
        web/status 401
        :title "No goodies for you"}
  unauthorized-secret-place-html []
  (hiccup/html
   [:h1 (get-meta :title)]
   [:p "Bad luck you failed."]
  ))
