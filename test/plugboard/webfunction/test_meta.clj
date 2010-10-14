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

(ns plugboard.webfunction.test-meta
  (:use
   clojure.test
   clojure.contrib.with-ns
   )
  (:require
   plugboard.webfunction.plugboards
   [plugboard.webfunction.webfunction :as web]
  ))

(def testing-ns (create-ns 'webfunction.test-meta.ns1))

(with-ns testing-ns

  (clojure.core/refer-clojure)
  
  (defn ^{plugboard.webfunction.webfunction/path "index.html"
          plugboard.webfunction.webfunction/content-type "text/html"
          plugboard.webfunction.webfunction/title "Index"}
    rep1 []
    (+ 2 2))

  (defn ^{plugboard.webfunction.webfunction/path "content.html"
          plugboard.webfunction.webfunction/content-type "text/html"
          plugboard.webfunction.webfunction/title "Index"}
    rep2 []
    (+ 2 2))

  (defn fn1 [] nil)
  (defn ^{:private true} fn2 [] nil)
  (defn ^{:dummy true} fn3 [] nil)
  )

(deftest test-is-web-namespace
  (is (true? (plugboard.webfunction.plugboards/is-web-namespace? (find-ns 'plugboard.webfunction.webfunction))))
  (is (false? (plugboard.webfunction.plugboards/is-web-namespace? (find-ns 'clojure.core))))
  )

(deftest test-is-web-function
  (is (true? (plugboard.webfunction.plugboards/is-web-function? (get (ns-publics testing-ns) 'rep1))))
  (is (true? (plugboard.webfunction.plugboards/is-web-function? (get (ns-publics testing-ns) 'rep2))))
  (is (false? (plugboard.webfunction.plugboards/is-web-function? (get (ns-publics testing-ns) 'fn1))))
  (is (false? (plugboard.webfunction.plugboards/is-web-function? (get (ns-publics testing-ns) 'fn3))))
  )

(deftest test-get-web-functions
  (is (= 2 (count (plugboard.webfunction.plugboards/get-web-functions testing-ns))))
  )
