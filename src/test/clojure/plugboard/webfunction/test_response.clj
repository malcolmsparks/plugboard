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

(ns plugboard.webfunction.test-response
  (:use
   clojure.test
   clojure.pprint
   plugboard.util
   ring.middleware.params)
  (:require
   [plugboard.core.plugboard :as plugboard]
   plugboard.webfunction.plugboards
   plugboard.webfunction.webfunction
   [clojure.xml :as xml]
   [clojure.zip :as zip]
   [clojure.data.zip.xml :as zf]
   [clojure.java.io :as io]))

(def testing-ns (create-ns 'plugboard.webfunction.test-body.ns))

(with-ns testing-ns
  (clojure.core/refer-clojure)
  (require 'hiccup.core)
  (require 'plugboard.webfunction.webfunction)

  (defn ^{plugboard.webfunction.webfunction/path "index.html"
          plugboard.webfunction.webfunction/content-type "text/html"
          :title "Title"}
    rep1 []
    (with-out-str
      (hiccup.core/html
       [:body
        [:h1 (plugboard.webfunction.webfunction/get-meta :title)]
        [:p {:id "query-param"} (plugboard.webfunction.webfunction/get-query-param "fish")]]))))

(deftest test-content-type
  (is (= "text/html"
         (get
          (plugboard.webfunction.plugboards/get-headers-from-webfn (get (ns-publics testing-ns) 'rep1))
          "Content-Type"))))

(def plugboard
     (plugboard/merge-plugboards
      plugboard/default-wiring
      (plugboard.webfunction.plugboards/web-function-resources [testing-ns])))

(def request {:uri "/index.html" :route-params {:* "index.html"} :request-method :get :query-string "fish=Herring"})

(deftest test-index-response
  (let [handler (wrap-params (plugboard.webfunction.plugboards/create-response-handler plugboard))
        response (handler request)
        doc (zip/xml-zip (xml/parse
                          (org.xml.sax.InputSource.
                           (java.io.StringReader. (:body response)))))]
    (is (= 200 (:status response)))
    (is (= "Title" (zf/xml1-> doc :h1 zf/text)))
    (is (= "Herring" (zf/xml1-> doc :p [(zf/attr= :id "query-param")] zf/text)))))


