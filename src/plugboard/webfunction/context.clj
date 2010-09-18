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

(ns plugboard.webfunction.context
  (:require [plugboard.webfunction.webfunction :as web])
  )

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

(defn get-meta [k]
  (get-in *web-context* [:meta k])
  )

(defn get-query-param [k]
  (get-in *web-context* [:request :query-params k])
  )

