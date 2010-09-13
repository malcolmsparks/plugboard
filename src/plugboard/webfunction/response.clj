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

(ns plugboard.webfunction.response
  (:require [plugboard.webfunction.webfunction :as web]
            [plugboard.webfunction.context :as context]
            [plugboard.core.plugboard :as plugboard]
            plugboard.webfunction.plugboards
            )
  )

(defn get-content-type [webfn]
  (let [ct (get (meta webfn) web/content-type)] ; TODO: Replace with if-let?
    (if ct {"Content-Type" ct} {})
    ))

;; TODO: See plugboard.webfunction.plugboards for a better approach which
;; replaces the use of this hard-coded location header. A better design would be
;; to allow webfunctions to return maps and/or to allow plugins to set headers
;; via the state which then become part of the response.

(defn- get-location-header [state]
  (let [location (get state :location)]
    (if location {"Location" location} {})
    ))

(defn get-headers [state webfn]
  (merge
   (get-content-type [webfn])
   (get-location-header state)
  ))

(defn get-body [status request webfn]
  (if (not (nil? webfn))
    (with-bindings {(var context/*web-context*)
                    {:status status :request request :meta (meta webfn)}}
      (webfn))))

(defn get-response [req plugboard]
  (let [[status state] (plugboard/get-status-with-state plugboard {:request req :response {:headers {}}})
        webfn (first (get state plugboard.webfunction.plugboards/compatible-webfunctions))
        headers (get-in state [:response :headers])
        body (get-body status req webfn)
        ]
    {:status status :headers headers :body body}
    ))

;; This creates a handler that can be wrapped in ring middleware.
(defn create-response-handler [plugboard]
  (fn [req]
    (get-response req plugboard)))