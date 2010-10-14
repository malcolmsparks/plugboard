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
  (:require
   [plugboard.webfunction.webfunction :as web]
   [hiccup.core :as hiccup]
   [clojure.java.io :as io]
   [clout.core :as clout]
   )
  )

(def favorites (ref {}))

(defn ^{web/path "index.html"
        web/content-type "text/html"
        :title "Forms demo"}
  index-html []
  (hiccup/html
   [:body
    [:h1 (web/get-meta :title)]

    (if (> (count @favorites) 0)
      [:h2 "Current favorites"])
    (map #(vector :p  [:a {:href (str "resources/" (first %) "/resource.html") } (first %) " - " (second %)])
         @favorites)

    [:h2 "Enter a new favorite"]

    [:form {:action "/forms/submit.html" :method "POST"}
     [:p "Thing: " [:input {:type "text" :name "key"}]]
     [:p "Favorite: " [:input {:type "text" :name "value"}]]
     [:p [:input {:type "submit"}]]
     ]]))

(def resource-route (clout/route-compile "resources/:key/resource.html"))

(defn match-document-route [uri]
  (clout/route-matches resource-route uri)
  )

(defn ^{web/path "submit.html"}
  submit-html []
  (let [key (web/get-form-param "key")
        value (web/get-form-param "value")
        new-resource-uri (web/create-uri (format  "resources/%s/resource.html" key))]
    (dosync (alter favorites assoc key value))
    {:headers {"Location" new-resource-uri}
     :body
     (hiccup/html
      [:body
       [:p "Thanks. You shouldn't see this, because the redirect should have kicked in."]])}))

(defn ^{web/path (fn [path]
                   (let [key (get (match-document-route path) "key")]
                     (not (nil? (find @favorites key))))
                   )}
  resource-html []
  (hiccup/html
   (let [key (get (match-document-route (web/get-path)) "key")
         value (get @favorites key)]

     [:body
      [:p "Your favorite " key " is a " value]
      ;; TODO: Construct with create-uri
      [:a {:href "../../index.html"} "Back to form"] 
      ]))
  )
