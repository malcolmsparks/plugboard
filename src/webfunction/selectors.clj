;; Copyright 2010 Malcolm Sparks.
;;
;; This file is part of Webfunction.
;;
;; Webfunction is free software: you can redistribute it and/or modify it under the
;; terms of the GNU Affero General Public License as published by the Free
;; Software Foundation, either version 3 of the License, or (at your option) any
;; later version.
;;
;; Webfunction is distributed in the hope that it will be useful but WITHOUT ANY
;; WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
;; A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
;; details.
;;
;; Please see the LICENSE file for a copy of the GNU Affero General Public License.

(ns webfunction.selectors)

(defn is-web-namespace? [ns]
  (= (find-ns 'webfunction.webfunction) ns)
  )

(defn is-web-function? [f]
  (-> f
      (meta)
      (keys)
      ((partial filter var?))
      ((partial map meta))
      ((partial map :ns))
      ((partial filter is-web-namespace?))
      (empty?)
      (not)))

(defn get-web-functions [ns]
  (-> ns
      (ns-publics)
      ((partial map second))
      ((partial filter is-web-function?))
      ))

