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

(ns webfunction.webfunction)

;; Define keys

(def ^{:private true} _uri nil)
(def uri (var _uri))

(def ^{:private true} _content-type nil)
(def content-type (var _content-type))

(def ^{:private true} _title nil)
(def title (var _title))

;; Context functions

(def *context* nil)

(defn get-title []
  (get-in *context* [:meta title])
  )

