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

(ns plugboard.webfunction.webfunction)

;; Define keys

(def ^{:private true} _status nil)
(def status (var _status))

(def ^{:private true} _path nil)
(def path (var _path))

(def ^{:private true} _content-type nil)
(def content-type (var _content-type))

(def ^{:private true} _title nil)
(def title (var _title))

;; Context functions

(def *web-context* nil)

(defn get-status []
  (get *web-context* :status)
  )

(defn ^{:doc "Create an absolute URI from the relative path. This allows web
  applications to be agnostic about where they exist in a contextual uri tree
  while still being able to create absolute URIs. Hence the context of the web
  application does not have to be encoded in the source code of the web
  application."}
  create-uri [path]
  (let [uri (get *web-context* [:request :uri])
        curr (get *web-context* [:request :route-params "*"])
        ]
    (str (apply str (take (- (count uri) (count curr)) uri)) path)
    ))

(defn get-request []
  (get *web-context* :request)
  )

(defn get-path []
  (get-in *web-context* [:request :route-params "*"])
  )

(defn get-request-body []
  (get-in *web-context* [:request :body])
  )

(defn get-meta [k]
  (get-in *web-context* [:meta k])
  )

(defn get-query-param [k]
  (get-in *web-context* [:request :query-params k])
  )

(defn get-form-param [k]
  (get-in *web-context* [:request :form-params k])
  )

(defn get-content-type []
  (get *web-context* :content-type)
  )
