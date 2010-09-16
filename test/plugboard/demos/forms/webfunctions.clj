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

(ns plugboard.demos.forms.webfunctions
  (:use
   plugboard.webfunction.context
   )
  (:require
   [plugboard.webfunction.webfunction :as web]
   [hiccup.core :as hiccup]
   )
  )

(defn ^{web/uri "/forms/index.html"
        web/content-type "text/html"
        :title "Forms demo"}
  index-html []
  (hiccup/html
   [:h1 (get-meta :title)]
   [:form {:action "/forms/submit.html" :method "POST"}
    [:input {:type "text"}]
    [:input {:type "submit"}]
    ]))

(defn ^{web/uri "/forms/submit.html"}
  submit-html []
  "Thanks. You shouldn't see this, because the redirect should have kicked in."
  )

(defn ^{web/uri "/forms/new-resource.html"}
  new-resource-html []
  "This is your new resource. TODO: let's add it to memory in submit.html so we can display it here."
  )
