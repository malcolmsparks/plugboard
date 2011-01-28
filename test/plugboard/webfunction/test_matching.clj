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

(ns plugboard.webfunction.test-matching
  (:use
   clojure.test
   clojure.contrib.with-ns
   plugboard.webfunction.plugboards))

(def testing-ns1 (create-ns 'plugboard.webfunction.test-matching.ns1))
(def testing-ns2 (create-ns 'plugboard.webfunction.test-matching.ns2))

(with-ns testing-ns1

  (clojure.core/refer-clojure)
  
  (defn ^{plugboard.webfunction.webfunction/path "index.html"
          plugboard.webfunction.webfunction/content-type "text/html"}
    rep1 []
    (+ 2 2))

  (defn ^{plugboard.webfunction.webfunction/path "index.html"
          plugboard.webfunction.webfunction/content-type "application/xml"}
    rep2 []
    (+ 2 2))

  (defn ^{plugboard.webfunction.webfunction/path "content.html"
          plugboard.webfunction.webfunction/content-type "application/xml"}
    rep3 []
    (+ 2 2)))

(with-ns testing-ns2

  (clojure.core/refer-clojure)
  
  (defn ^{plugboard.webfunction.webfunction/path "index.html"
          plugboard.webfunction.webfunction/content-type "text/html"}
    rep1 []
    (+ 2 2)))

(deftest test-index-html-matches-two-web-functions
  (is (webfn-matches-path? "index.html" (get (ns-publics testing-ns1) 'rep1)))
  (is (webfn-matches-path? "index.html" (get (ns-publics testing-ns1) 'rep2)))
  (is (not (webfn-matches-path? "index.html" (get (ns-publics testing-ns1) 'rep3)))))

(deftest test-get-matching-webfunctions-for-path
  (is (= 2 (count (get-matching-webfunctions-for-path "index.html" [testing-ns1]))))
  (is (= 1 (count (get-matching-webfunctions-for-path "content.html" [testing-ns1]))))
  (is (= 3 (count (get-matching-webfunctions-for-path "index.html" [testing-ns1 testing-ns2])))))
