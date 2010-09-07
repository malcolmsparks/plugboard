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

(ns webfunction.context
  (:require [webfunction.webfunction :as web])
  )

(def *web-context* nil)

(defn get-status []
  (get *web-context* :status)
  )

(defn get-request []
  (get *web-context* :request)
  )

(defn get-meta [k]
  (get-in *web-context* [:meta k])
  )

(defn get-query-param [k]
  (get-in *web-context* [:request :query-params k])
  )

