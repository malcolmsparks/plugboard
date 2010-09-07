;; Copyright 2010 Malcolm Sparks.
;;
;; This file is part of Webfunction.
;;
;; Webfunction is free software: you can redistribute it and/or modify it under the
;; terms of the GNU Affero General Public License as published by the Free
;; Software Foundation, either version 3 of the License, or (at your option) any
;; later version.
;;
;; Webfunction is distributed in the hope that it will be useful but WITHOUT ANY
;; WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
;; A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
;; details.
;;
;; Please see the LICENSE file for a copy of the GNU Affero General Public License.

(ns webfunction.test-response
  (:use
   clojure.test
   clojure.contrib.with-ns
   webfunction.response
   ring.middleware.params
   )
  (:require
   plugboard.configurations
   webfunction.plugboards
   [hiccup.core :as hiccup]
   [clojure.xml :as xml]
   [clojure.zip :as zip]
   [clojure.contrib.zip-filter.xml :as zf]
   [clojure.java.io :as io]
   )
  )

(def testing-ns (create-ns 'webfunction.test-body.ns))

(with-ns testing-ns

  (clojure.core/refer-clojure)
  (require 'hiccup.core)
  (require 'webfunction.webfunction)
  
  (defn ^{webfunction.webfunction/uri "/index.html"
          webfunction.webfunction/content-type "text/html"
          :title "Title"}
    rep1 []
    (hiccup.core/html
     [:body
      [:h1  (webfunction.context/get-meta :title)]
      [:p#query-param (webfunction.context/get-query-param "fish")]
      ]
     )
    )
  )

(deftest test-content-type
  (is (= "text/html"
         (get
          (get-content-type (get (ns-publics testing-ns) 'rep1))
          "Content-Type"))))

(def plugboard
     (merge plugboard.configurations/default-decision-map
            (webfunction.plugboards/web-function-resources [testing-ns])
            ))

(def request {:uri "/index.html" :request-method :get :query-string "fish=Herring"})

(deftest test-index-response
  (let [handler (wrap-params (create-response-handler plugboard) :query-string)
        response (handler request)
        doc (zip/xml-zip (xml/parse
                          (org.xml.sax.InputSource.
                           (java.io.StringReader. (:body response)))))
        ]
    (is (= 200 (:status response)))
    (is (= "Title" (zf/xml1-> doc :h1 zf/text)))
    (is (= "Herring" (zf/xml1-> doc :p [(zf/attr= :id "query-param")] zf/text)))
    ))
