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

(ns plugboard.demos.jetty-fixture
  (:require
   ring.adapter.jetty
   ring.middleware.params
   plugboard.demos.query-params.configuration
   [clojure.xml :as xml]
   [clojure.zip :as zip]
   ))

(defn get-jetty-port [] 8083)

(defn create-handler [plugboard]
  (fn [req]
    (plugboard.webfunction.response/get-response req plugboard)
    ))

(defn run-jetty [routes]
  (ring.adapter.jetty/run-jetty
   routes
   {:join? false :port (get-jetty-port)}
   ))

(defn make-fixture [routes]
  (fn [f]
    (let [jetty (run-jetty routes)]
      (try 
        (f)
        (finally
         (.stop jetty))))
  ))

(defn body-zip [response]
  (zip/xml-zip (xml/parse
                (org.xml.sax.InputSource.
                 (java.io.StringReader. (:body response))))))

