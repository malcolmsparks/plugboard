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

(ns plugboard.webfunction.plugboards
  (:use clojure.contrib.trace)
  (:require [plugboard.webfunction.webfunction :as web]
            [clojure.contrib.condition :as condition]
            [plugboard.core.plugboard :as plugboard]
            clojure.contrib.base64
            )
  )

(def ^{:private true} _wn)
(def
 ^{:doc "A vector of namespaces that contain web functions."}
 web-namespaces (var _wn))

(def ^{:private true} _newlocation)
(def
 ^{:doc "The location of the newly appended resource. Used for redirects."}
 new-location (var _newlocation))

;; --------------------------------------------------------------------------------


(defn is-web-namespace? [ns]
  (= (find-ns 'plugboard.webfunction.webfunction) ns)
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

;; --------------------------------------------------------------------------------

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
    (with-bindings {(var web/*web-context*)
                    {:status status :request request :meta (meta webfn)}}
      (webfn))))

(defn initialize-state [req]
  {:request req :response {:headers {}}}
  )

(defn- webfn-matches-path-or-nil? [path webfn]
  (let [p (get (meta webfn) web/path)]
    (cond
     (nil? p) true                    ; it's a match if it's not specified.
     (fn? p) (true? (p path))
     (string? p) (= p path)
     :otherwise false))
  )

(defn- webfn-matches-status? [status webfn]
  (let [s (get (meta webfn) web/status)]
    (cond
     (fn? s) (true? (s status))
     (number? s) (= s status)
     ;; if there is no status declared we select the function if the
     ;; status is not an error.
     :otherwise (< status 400)))
  )

(defn- webfn-matches? [path status webfn]
  (and
   (webfn-matches-path-or-nil? path webfn)
   (webfn-matches-status? status webfn)
   )
  )

(defn get-matching-webfunctions [path status namespaces]
  (mapcat
   (fn [web-ns]
     (filter #(webfn-matches? path status %)
             (plugboard.webfunction.plugboards/get-web-functions
              web-ns)))
   namespaces)
  )

(defn get-response [req plugboard]
  (let [[status state] (plugboard/get-status-with-state plugboard
                         (initialize-state req))
        namespaces (get state web-namespaces)
        webfns (get-matching-webfunctions (get state plugboard/path) status namespaces)
        webfn (first webfns)
        headers (merge (get-in state [:response :headers] (get-headers-from-webfn webfn)))
        body (get-body status req webfn)
        ]
    (if (map? body)
      {:status status :headers (merge headers (:headers body)) :body (:body body)}
      {:status status :headers headers :body body}
      )
    ))

;; This creates a handler that can be wrapped in ring middleware.
(defn create-response-handler [plugboard]
  (fn [req]
    (get-response req plugboard)))

;; --------------------------------------------------------------------------------

;; This is almost identical to
;; plugboard.webfunction.response/webfn-matches-path? but doesn't
;; count functions that don't have paths.
;; TODO: Try to re-factor so that only one such function is needed?
(defn webfn-matches-path? [path webfn]
  (let [p (get (meta webfn) web/path)]
    (cond
     (fn? p) (true? (p path))
     (string? p) (= p path)
     :otherwise false))
  )

(defn get-matching-webfunctions-for-path [path web-namespaces]
  (mapcat
   (fn [web-ns]
     (filter #(webfn-matches-path? path %)
             (plugboard.webfunction.plugboards/get-web-functions
              web-ns)))
   web-namespaces)
  )

(defn web-function-resources [namespaces]
  {
   :init (fn [state]
           (-> state
               (assoc web-namespaces namespaces)
               (assoc plugboard/path (get-in state [:request :route-params "*"]))
               ))

   plugboard/resource-exists?
   (fn [state dlg]
     [(not (empty? (get-matching-webfunctions-for-path (get state plugboard/path) (get state web-namespaces)))) state]
     )
   })

(defn set-header [state name value]
  (update-in state [:response :headers] (fn [old] (assoc old name value)))
  )

(defn welcome-page [path]
  {plugboard/resource-previously-existed?
   (fn [state dlg] (= \/ (last (get-in state [:request :uri]))))
   plugboard/resource-moved-permanently?
   (fn [state dlg] [true
                   (let [location (str (get-in state [:request :uri]) path)]
                     (set-header state "Location" location))])})

(defn redirect-to-new-resource []
  {
   plugboard/redirect? (fn [state dlg] true)
   }
  )

;; The auth string arrives as "Basic user:password" (where user:password is base64 encorded
(defn compare-secret [expected auth-string]
  (let [actual (second (re-seq #"[\w=]+" auth-string))]
    (= expected actual)
    ))

(defn basic-authentication [realm requires-auth-fn user password]
  {plugboard/authorized?
   (let [encoded (clojure.contrib.base64/encode-str (str user ":" password))]
     (fn [state dlg]
       (if (requires-auth-fn (get state :request))
         (let [res
               (and
                (contains? (get-in state [:request :headers]) "authorization")
                (compare-secret encoded (get-in state [:request :headers "authorization"])))]
           (if res true
               [false (set-header state "WWW-Authenticate" (format "Basic realm=\"%s\"" realm))])
           )
         true
         )))})
