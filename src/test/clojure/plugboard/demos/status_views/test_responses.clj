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

(ns plugboard.demos.status-views.test-responses
  (:use
   clojure.test
   compojure.core
   plugboard.demos.jetty-fixture
   clojure.contrib.zip-filter.xml)
  (:require
   [clj-http.client :as http]
   plugboard.demos.links.configuration
   clj-http.core
   )
  )

(defroutes main-routes
  (GET "/status-views/*" []
       (create-handler (plugboard.demos.status-views.configuration/create-plugboard))))

(use-fixtures :once (make-fixture main-routes))

;; This is copied from clj-http.client but with exceptions removed.
(def request
  (-> #'clj-http.core/request
    http/wrap-redirects
;;    wrap-exceptions
    http/wrap-decompression
    http/wrap-input-coercion
    http/wrap-output-coercion
    http/wrap-query-params
    http/wrap-basic-auth
    http/wrap-accept
    http/wrap-accept-encoding
    http/wrap-content-type
    http/wrap-method
    http/wrap-url))

(defn webget
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :get :url url})))

(deftest test-demo
  (let [response (webget (format "http://localhost:%d/status-views/missing.html" (get-jetty-port)))
;;        doc (body-zip response)
        ]
    (is (= 404 (get response :status)))
    )
  )
