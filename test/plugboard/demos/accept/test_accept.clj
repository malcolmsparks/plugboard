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

(ns plugboard.demos.accept.test-accept
  (:use clojure.test)
  (:require
   [clj-http.client :as http]
   [clojure.contrib.zip-filter.xml :as zfx]
   [compojure.core :as compojure]
   [plugboard.core.conneg :as conneg]
   [plugboard.demos.accept.webfunctions]
   [plugboard.demos.jetty-fixture :as jf]
   [plugboard.webfunction.plugboards :as plugboards]
   [plugboard.webfunction.webfunction :as web]))

;; TODO: Do more tests to check different content negotiation situations.

(use-fixtures :once
              (jf/make-fixture
               (compojure/routes
                (compojure/GET "/test/*" []
                               (jf/create-handler (plugboard.demos.accept.configuration/create-plugboard))))))

(deftest test-demo
  (letfn [(get-content-type [response] (:type (conneg/accept-fragment (get-in response [:headers "content-type"]))))]
    (let [response (http/get (format "http://localhost:%d/test/index" (jf/get-jetty-port)))
          doc (jf/body-zip response)]
      (is (= 200 (:status response)))
      (is (= ["application" "xhtml+xml"] (get-content-type response))))))
