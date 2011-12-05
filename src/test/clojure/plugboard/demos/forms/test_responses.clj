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

(ns plugboard.demos.forms.test-responses
  (:use
   clojure.test
   compojure.core
   plugboard.demos.jetty-fixture
   clojure.data.zip.xml
   plugboard.util)
  (:require
   [clj-http.client :as http]
   [ring.util.codec :as codec]
   [clojure.string :as string]
   plugboard.demos.forms.configuration))

(defroutes main-routes
  (ANY "/forms/*" []
       (ring.middleware.params/wrap-params
        (create-handler (plugboard.demos.forms.configuration/create-plugboard)))))

(use-fixtures :once (make-fixture main-routes))

(deftest test-get
  (let [url "http://localhost:%d/forms/index.html"
        response (http/get (format url (get-jetty-port)))
        form-doc (body-zip response)
        form (xml1-> form-doc :form)]
    (is (= 200 (get response :status)))
    (is (not (nil? form)))))

(deftest test-post
  (dosync (ref-set plugboard.demos.forms.webfunctions/favorites {}))
  (is (empty? @plugboard.demos.forms.webfunctions/favorites))
  (let [url "http://localhost:%d/forms/submit.html"
        response (post-form (format url (get-jetty-port)) {"key" "fish"
                                                           "value" "herring"
                                                           "submit" "submit"})]
    (is (= 200 (get response :status)))
    (is (= @plugboard.demos.forms.webfunctions/favorites {"fish" "herring"}))))

