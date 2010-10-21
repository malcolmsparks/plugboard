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

(ns plugboard.core.test-merge
  (:use
   clojure.test
   plugboard.core.plugboard
   )
  )

(deftest test-can-override-resource-exists
  (is (= 200 (get-status (merge-plugboards default-wiring {resource-exists? true})
                         {:request {:request-method :get}}))))

(deftest test-merge
  (is (= {:A1 '(true)}
         (merge-plugboards {:A1 true})))
  (is (= {:A1 '(true false) :B1 '(false true)}
         (merge-plugboards {:A1 true :B1 false} {:A1 false :B1 true})))
  (is (= {:A1 '(true false)}
         (merge-plugboards {:A1 true} {:A1 false})))
  (is (= {:A1 '(true) :B1 '(true)}
         (merge-plugboards {[:A1 :B1] true})))
  (is (= {:A1 '(true) :B1 '(true false)}
         (merge-plugboards {[:A1 :B1] true} {:B1 false})))
  )


