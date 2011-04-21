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

(ns plugboard.demos.main
  (:use compojure.core)
  (:require
   plugboard.demos.menu
   plugboard.demos.hello-world.configuration
   plugboard.demos.query-params.configuration
   plugboard.demos.links.configuration
   plugboard.demos.forms.configuration
   plugboard.demos.status-views.configuration
   plugboard.demos.basic-auth.configuration
   plugboard.demos.custom-auth.configuration
   plugboard.demos.accept.configuration
   [compojure.route :as route]
   ring.middleware.params
   [clojure.contrib.find-namespaces :as find-ns]
   [clojure.contrib.classpath :as cp]
   [clojure.string :as str]))

(defn create-handler [plugboard]
  (fn [req]
    (plugboard.webfunction.plugboards/get-response req plugboard)))

(defn create-routes []
  (letfn [(add-demos-dir [d] (java.io.File. d "plugboard/demos"))
          (get-demos-dir [] (->> (cp/classpath-directories)
                                 (filter #(.exists (add-demos-dir %)))
                                 (map #(add-demos-dir %))
                                 first))
          (spl [sym] (re-seq #"[A-Za-z0-9-]+" (str sym)))
          (_require [c] (require (:sym c)) c)
          (get-ns [c] (assoc c :ns (find-ns (:sym c))))
          (find-fn [c] (assoc c :fn (get (ns-publics (:ns c)) 'create-plugboard)))
          (add-path [c] (assoc c :path (format "/%s/*" (second (reverse (:components c))))))
          (add-route [c] (assoc c :route (GET (:path c) [] (create-handler ((:fn c))))))
          (add-index [routes] (cons (GET "/" [] (fn [req] (plugboard.demos.menu/render-page))) routes))
          (add-404 [routes] (concat routes (list (route/not-found "<h1>Page not found</h1>"))))
          ]
    (->>
     (get-demos-dir)
     .listFiles
     (filter #(.isDirectory %))
     (mapcat find-ns/find-namespaces-in-dir)
     (map #(hash-map :sym % :components (spl %)))
     (filter #(= "configuration" (last (:components %))))
     (map (apply comp (reverse (vector _require get-ns find-fn add-path add-route))))
     (map :route)
     add-index
     add-404
     (apply routes))))


(defroutes main-routes
  (GET "/" [] (fn [req] (plugboard.demos.menu/render-page)))
  (GET "/hello-world/*" []
       (create-handler (plugboard.demos.hello-world.configuration/create-plugboard)))
  (GET "/query-params/*" []
       (ring.middleware.params/wrap-params
        (create-handler (plugboard.demos.query-params.configuration/create-plugboard))))
  (GET "/links/*" []
       (create-handler (plugboard.demos.links.configuration/create-plugboard)))
  (ANY "/forms/*" []
       (ring.middleware.params/wrap-params
        (create-handler (plugboard.demos.forms.configuration/create-plugboard))))
  (GET "/status-views/*" []
       (create-handler (plugboard.demos.status-views.configuration/create-plugboard)))
  (GET "/basic-auth/*" []
       (create-handler (plugboard.demos.basic-auth.configuration/create-plugboard)))
  (GET "/custom-auth/*" []
       (create-handler (plugboard.demos.custom-auth.configuration/create-plugboard)))
  (GET "/accept/*" []
       (create-handler (plugboard.demos.accept.configuration/create-plugboard)))
  (route/not-found "<h1>Page not found</h1>"))
