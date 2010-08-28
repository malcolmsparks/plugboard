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

(ns plugboard.controller
  #^{:author "Malcolm Sparks"
     :doc "Handle the incoming request and delegate to more specialised
controllers as appropriate."  }
  (:use
   clout.core
   compojure.response
   plugboard.util
   plugboard.status
   plugboard.response
   )
  (:require
   ring.middleware.reload
   ring.middleware.stacktrace
   ring.middleware.params
   ))

(defn- get-location-header [state]
  (let [location (get state :location)]
    (if location {"Location" location} {})
    ))

(defn- guess-content-type [uri]
  (case uri
        "html" "text/html; charset=iso-8859-1"
        "xhtml" "application/xhtml+xml; charset=iso-8859-1"
        "css" "text/css; charset=iso-8859-1"
        "png" "image/png"
        "js" "text/javascript; charset=iso-8859-1"
        ))

(defn- get-response-headers [state]
  (merge (get-location-header state) 
         {"Content-Type" (guess-content-type 
                          (get-suffix (get-in state [:request :uri])))
          "X-Lang" "Clojure"
          })
)

(defstruct controller-def
  :status-overrides ; A function which returns a mapping between decision point codes and decision functions.
  :view-overrides ; A function which returns a mapping between status codes and view functions.
  )

(defn- create-response [request controller-def]
  (let [[status state] (get-status ((:status-overrides controller-def))
                                   {:request request})
        view (create-view status state ((:view-overrides controller-def)))
        ]
    {:status status ; TODO: Allow the view to override the status?
      :headers (merge (get-response-headers state) (:headers view))
      :body (:body view)}
     ))

(defn create-controller
  [controller-def]
  (fn
    [request]
    (render request (create-response request controller-def))
    ))

(defn create-handler [& mappings]
  "Create a handler that will try each mapping in turn until it finds a match."
  (fn [request]
    (some
     (fn [mapping]
       (if-let [route-params (route-matches (first mapping) request)]
         ((second mapping) ; call the sub-handler
          ;; with a request that includes route parameters.
          (merge-with merge request {:route-params route-params})
          )))
     mappings)))

(defn not-found-handler [request]
  "A temporary catch-all handler, to be replaced by something more sophisticated."
  {:status 404 :headers {} :body "Not found"}
  )

(defn create-root-handler [handlers]
  #^{:doc
     "These are the predetermined routes, mapped to handlers which further
        process the request and provide the response."}
  (apply create-handler
         (concat handlers
                 [["*" not-found-handler]])))

(defn create-app [handlers]
  #^{:doc
     "Define the structure of the root application. This matches the URL
     against predetermined routes to identify which handler to delegate
     responsibility to. It creates the main handler and then wraps it in a
     number of other handlers. The reason we refer to (var resources) is because
     the '->' macro is expanded before evaluation and if we in-lined the
     resources the form would get called multiple times."}
  (let [h (create-root-handler handlers)]
    (-> h
        (ring.middleware.reload/wrap-reload
         '( ; Reload if any of these modules change.
           plugboard.controller
           plugboard.response
           plugboard.status
           plugboard.util
           ))
        (ring.middleware.params/wrap-params) ; Parse query-string parameters.
        (ring.middleware.stacktrace/wrap-stacktrace) ; Emit a stack-trace if something goes wrong.
        )))
