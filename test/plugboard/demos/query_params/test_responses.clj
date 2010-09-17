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

(ns plugboard.demos.query-params.test-responses
  (:use clojure.test compojure.core)
  (:require
   ring.adapter.jetty
   plugboard.demos.query-params.configuration
   [clj-http.client :as client]
   [clojure.xml :as xml]
   [clojure.zip :as zip]
   [clojure.contrib.zip-filter.xml :as zf]
   )
  )

(def port 8083)

(defn create-handler [plugboard]
  (fn [req]
    (plugboard.webfunction.response/get-response req plugboard)
    ))

(defn run-jetty []
  (ring.adapter.jetty/run-jetty

   (defroutes main-routes
     (GET "/query-params/*" []
          (create-handler (plugboard.demos.query-params.configuration/create-plugboard))))
   {:join? false :port port}
   ))

(defn jetty [f]
  (let [jetty (run-jetty)]
    (try 
      (f)
      (finally
       (.stop jetty)))))


(use-fixtures :once jetty)

(defn body-zip [response]
  (zip/xml-zip (xml/parse
                (org.xml.sax.InputSource.
                 (java.io.StringReader. (:body response))))))

(deftest test-demo
  (let [response (client/get
                  (format "http://localhost:%d/query-params/query.html?coffee=Latte" port))
        doc (body-zip response)
        ]
    (= 200 (get response :status))
    (is (= "You chose a Latte" (zf/xml1-> doc :body :h1 zf/text)))
    )
  )
