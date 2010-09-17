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
  (:use
   clojure.test compojure.core
   plugboard.demos.jetty-fixture
   clojure.contrib.zip-filter.xml)
  (:require
   [clj-http.client :as http]
   )
  )

(defroutes main-routes
     (GET "/query-params/*" []
          (create-handler (plugboard.demos.query-params.configuration/create-plugboard))))

(use-fixtures :once (make-fixture main-routes))

(deftest test-demo
  (let [response (http/get
                  (format "http://localhost:%d/query-params/query.html?coffee=Latte" (get-jetty-port)))
        doc (body-zip response)]
    (is (= 200 (get response :status)))
    (is (= "You chose a Latte" (xml1-> doc :body :h1 text)))
    )
  )
