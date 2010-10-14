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

(ns plugboard.demos.hello-world.test-responses
  (:use
   clojure.test compojure.core
   plugboard.demos.jetty-fixture
   clojure.contrib.zip-filter.xml)
  (:require
   [clj-http.client :as http]
   plugboard.demos.hello-world.configuration
   )
  )

(defroutes main-routes
     (GET "/hello-world/*" []
          (create-handler (plugboard.demos.hello-world.configuration/create-plugboard))))

(use-fixtures :once (make-fixture main-routes))

;; TODO: Test welcome page functionality separately

(deftest test-demo
  (let [response (http/get (format "http://localhost:%d/hello-world/index.html" (get-jetty-port)))
        doc (body-zip response)]
    (is (= 200 (get response :status)))
    (is (= "Hello World!" (xml1-> doc :body :h1 text)))
    )
  )
