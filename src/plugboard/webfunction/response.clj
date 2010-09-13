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
  (if-let [ct (get (meta webfn) web/content-type)]
    {"Content-Type" ct}
    {})
    )

(defn get-headers-from-webfn [webfn]
  (get-content-type [webfn])
  )

(defn get-body [status request webfn]
  (if (not (nil? webfn))
    (with-bindings {(var context/*web-context*)
                    {:status status :request request :meta (meta webfn)}}
      (webfn))))

(defn initialize-state [req]
  {:request req :response {:headers {}}}
  )

(defn get-response [req plugboard]
  (let [[status state] (plugboard/get-status-with-state plugboard
                         (initialize-state req))
        webfn (first (get state plugboard.webfunction.plugboards/compatible-webfunctions))
        headers (merge (get-in state [:response :headers] (get-headers-from-webfn webfn)))
        body (get-body status req webfn)
        ]
    {:status status :headers headers :body body}
    ))

;; This creates a handler that can be wrapped in ring middleware.
(defn create-response-handler [plugboard]
  (fn [req]
    (get-response req plugboard)))