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
  (:use
   clojure.test)
  (:require
   [plugboard.core.conneg :as conneg]
   [plugboard.webfunction.webfunction :as web]
   [plugboard.webfunction.plugboards :as plugboards]
   [plugboard.demos.accept.webfunctions]))

(deftest test-accept
  (let [accepts (map :type (conneg/sorted-accept "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"))]
    (is (= 6 (count accepts)))
    (let [fn-list (plugboards/negotiate-content
                   (plugboards/get-web-functions (find-ns 'plugboard.demos.accept.webfunctions))
                   accepts
                   plugboards/get-content-type-fragment
                   )]
      (println (first fn-list))
      (is (= 6 (count fn-list))))))


;; TODO: Do more tests to check different content negotiation situations.

