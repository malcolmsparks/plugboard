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
            [plugboard.webfunction.plugboards :as plugboards]
            )
  )


(defn ^{:doc "Get the content type from the web function metadata"}
  get-content-type [webfn]
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

(defn webfn-matches-path? [path webfn]
  (let [uri (get (meta webfn) web/uri)]
    (cond
     (nil? uri) true                    ; it's a match if it's not specified.
     (fn? uri) (not (nil? (uri path)))
     (string? uri) (= uri path)
     :otherwise false))
  )

(defn webfn-matches-status? [status webfn]
  (let [s (or (get (meta webfn) web/status) 200)]
    (cond
     (fn? s) (true? (s status))
     (number? s) (= s status)
     :otherwise false))
  )

(defn webfn-matches? [path status webfn]
  (and
   (webfn-matches-path? path webfn)
   (webfn-matches-status? status webfn)
   )
  )

(defn get-matching-webfunctions [path status namespaces]
  (mapcat
   (fn [web-ns]
     (filter #(webfn-matches? path status %)
             (plugboard.webfunction.selectors/get-web-functions
              web-ns)))
   namespaces)
  )

(defn get-response [req plugboard]
  (let [[status state] (plugboard/get-status-with-state plugboard
                         (initialize-state req))
        namespaces (get state plugboards/web-namespaces)
        webfns (get-matching-webfunctions (get state plugboard/path) status namespaces)
        webfn (first webfns)
        headers (merge (get-in state [:response :headers] (get-headers-from-webfn webfn)))
        body (get-body status req webfn)
        ]
    {:status status :headers headers :body body}
    ))

;; This creates a handler that can be wrapped in ring middleware.
(defn create-response-handler [plugboard]
  (fn [req]
    (get-response req plugboard)))