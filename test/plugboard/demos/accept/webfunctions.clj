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

(ns plugboard.demos.accept.webfunctions
  (:require
   [plugboard.webfunction.webfunction :as web]
   [plugboard.webfunction.html :as html]
   [hiccup.core :as hiccup]
   [clojure.contrib.prxml :as prxml]))

(defn ^{web/path "index"
        web/content-type "application/xhtml+xml"}
  index-xml []
  (with-out-str
    (binding [prxml/*prxml-indent* 4]
      (prxml/prxml [:decl!]
       [:html
        [:head]
        [:body
         [:h1 "Index"]
         [:p "Content type is " (str (web/get-content-type))]
         (html/table (web/get-request))
         ]]))))

(defn ^{web/path "index"
        web/content-type "text/html"
        :title "Accept demo - text/html"}
  index-html []
  (hiccup/html
   [:html
    [:head]
    [:body
     [:h1 "Index"]
     [:p "Content type is " (str (web/get-content-type))]
     (html/table (web/get-request))
     ]]))

(defn ^{web/path "index"
        web/content-type "text/plain"
        :title "Accept demo - text/plain"}
  index-plain []
  "Accept demo")

