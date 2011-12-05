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
   clojure.test)
  (:require
   [clojure.data.zip.xml :as zfx]
   [compojure.core :as compojure]
   [clj-http.client :as http]
   plugboard.demos.hello-world.configuration
   [plugboard.demos.jetty-fixture :as jf]))


(use-fixtures :once
              (jf/make-fixture
               (compojure/routes
                (compojure/GET "/test/*" []
                               (jf/create-handler (plugboard.demos.hello-world.configuration/create-plugboard))))))

;; TODO: Test welcome page functionality separately

(def x zfx/xml1->)
(def text zfx/text)

(deftest test-demo
  (let [response (http/get (format "http://localhost:%d/test/index.html" (jf/get-jetty-port)))
        doc (jf/body-zip response)]
    (is (= 200 (get response :status)))
    (is (= "Hello World!" (x doc :body :h1 text)))))
