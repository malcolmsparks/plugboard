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

(ns plugboard.core.test-decision
  (:use clojure.test
        plugboard.core.plugboard)
  )

(deftest test-decides
  (testing "Status is properly overridden"
    (are
     [expected-status fn-list]
     (= expected-status (first (get-plug-decision {} fn-list)))

     ;; If we have a list of alternate status codes, the last one prevails.
     300 [100 200 300]
     300 [
          (fn [state dlg] 100)
          (fn [state dlg] 200)
          (fn [state dlg] 300)]
     200 [100 200
          (fn [state dlg] (dlg state))]
     
     100 [100
          (fn [state dlg] (dlg state))
          (fn [state dlg] (dlg state))]
     
     100 [(fn [state dlg] 100)
          (fn [state dlg] (dlg state))
          (fn [state dlg] (dlg state))]

     ;; The same goes for booleans
     false [false true false]
     true [false true (fn [state dlg] (dlg state))
           ]
     false [true (fn [state dlg] (= 1 2))
            (fn [state dlg] (dlg state))
            ]
     true [true (fn [state dlg] (= 1 2))
            (fn [state dlg] (= 2 2))
            ]
     ))
  (testing "State can be overridden by functions"
    (are
     [initial-state fn-list expected-state]
     (= expected-state (second (get-plug-decision initial-state fn-list)))

     {:foo "foo"}
     [100 (fn [state dlg] [true (assoc state :bar "bar")])]
     {:foo "foo" :bar "bar"}

     {:foo "foo" :bar "bar"}
     [100 (fn [state dlg] [true (dissoc state :bar)])]
     {:foo "foo"}

     ;; Test we preserve the state additions of multiple plugins
     {:foo "foo"}
     [100
      (fn [state dlg] [true (assoc state :bar "bar")])
      (fn [state dlg] (dlg  (assoc state :zip "zip")))
      ]
     {:foo "foo" :bar "bar" :zip "zip"}

     ;; Test we preserve the state subtractions of multiple plugins
     {:foo "foo" :bar "bar" :zip "zip"}
     [100
      (fn [state dlg] [true (dissoc state :bar)])
      (fn [state dlg] (dlg  (dissoc state :zip)))
      ]
     {:foo "foo"}

     ;; It must be possible to return a pair which returns a status
     ;; code and altered state.
     {:foo "foo"}
     [100 (fn [state dlg] [200 (assoc state :bar "bar")])]
     {:foo "foo" :bar "bar"}
     )
    )
  )
