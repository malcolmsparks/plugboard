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

(ns plugboard.core.test-overrides
  (:use
   clojure.test
   )
  (:require
   [plugboard.core.plugboard :as plugboard]
   )
  )

(defn insert-path [state dlg]
  [false (merge {plugboard/path "a/b/index.html"} state)]
  )

(defn resource-exists [state dlg]
  (if (= "a/b/index.html" (get state plugboard/path))
    [true state]
    [false state]
  ))

(deftest path-insertion
  (is (= 404 (plugboard/get-status
              (plugboard/merge-plugboards
               plugboard/default-decision-map)
              {:request {:request-method :get}})))
  (is (= 200 (plugboard/get-status
              (plugboard/merge-plugboards
               plugboard/default-decision-map
               {:B3 insert-path :C7 resource-exists})
              {:request {:request-method :get}})))
    )



