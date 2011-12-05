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

(ns plugboard.demos.links.test-responses
  (:use
   clojure.test
   compojure.core
   plugboard.demos.jetty-fixture
   clojure.data.zip.xml)
  (:require
   [clj-http.client :as http]
   plugboard.demos.links.configuration
   )
  )

(defroutes main-routes
  (GET "/links/*" []
       (create-handler (plugboard.demos.links.configuration/create-plugboard))))

(use-fixtures :once (make-fixture main-routes))

(deftest test-demo
  (let [response (http/get (format "http://localhost:%d/links/index.html" (get-jetty-port)))
        doc (body-zip response)]
    (is (= 200 (get response :status)))
    ;; TODO: Find the link and 'click' on it. This will require some kind of
    ;; client robot test DSL which is able to check the contents of pages, divs,
    ;; tables, etc.. along with the ability to post forms and click on links.
    )
  )
