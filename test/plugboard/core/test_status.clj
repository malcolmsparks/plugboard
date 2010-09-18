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

(ns plugboard.core.test-status
  (:use
   clojure.test
   plugboard.core.plugboard
   )
  )

(deftest test-is-method
  (is ((is-web-method? :put) {:request {:request-method :put}} nil))
  (is (not ((is-web-method? :put) {:request {:request-method :post}} nil)))
  (is ((is-web-method? :get :head) {:request {:request-method :get}} nil))
  (is (not ((is-web-method? :get :head) {:request {:request-method :options}} nil)))
  )

(deftest test-header-exists
  (is ((header-exists? "If-Match") {:request {:headers {"If-Match" "*"}}} nil))
  (is (not ((header-exists? "If-Match") {:request {:headers {"Dummy" "true"}}} nil)))
  )

(deftest test-get-status
  (is (= 200 (get-status (merge-plugboards default-wiring) {:request {:request-method :options}})))
  (is (= 404 (get-status (merge-plugboards default-wiring) {:request {:request-method :get}})))
  )

;; TODO: Would be better to use a record to return the [status state] in.
;; TODO: Not sure about the above TODO - perhaps better that we don't use records.

(deftest test-can-override-resource-exists
  (is (= 200 (get-status (merge-plugboards default-wiring {:C7 true})
                         {:request {:request-method :get}}))))

