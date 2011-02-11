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
            [plugboard.core.conneg :as conneg]))

(def ^{:private true} _wn)
(def
 ^{:doc "A vector of namespaces that contain web functions."}
 web-namespaces (var _wn))

(def ^{:private true} _newlocation)
(def
 ^{:doc "The location of the newly appended resource. Used for redirects."}
 new-location (var _newlocation))

(def ^{:private true} _umwf)
(def
 ^{:doc "A vector of web functions that match the URI."}
 uri-matching-web-functions (var _umwf))

(def ^{:private true} _umwf)
(def
 ^{:doc "A vector of web functions that match the URI."}
 uri-matching-web-functions (var _umwf))

(defrecord ContentFunction [webfn content-type])

(defn is-web-namespace? [ns]
  (= (find-ns 'plugboard.webfunction.webfunction) ns))

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
  (map (fn [webfn] (ContentFunction. webfn (get (meta webfn) web/content-type)))
       (filter is-web-function? (map second (ns-publics ns)))))

;; --------------------------------------------------------------------------------

(defn
  ^{:doc "Get the content type from the web function metadata"}
  get-content-type-fragment [^ContentFunction cf]
  (:type (conneg/accept-fragment (get (meta (:webfn cf)) web/content-type))))

(defn get-headers-from-webfn [webfn]
  (if-let [ct (get (meta webfn) web/content-type)]
    {"Content-Type" ct}
    {}))

(defn get-body [status request webfn content-type]
  (if (not (nil? webfn))
    (with-bindings {(var web/*web-context*)
                    {:status status :request request :meta (meta webfn) :content-type content-type}}
      (webfn))))

(defn initialize-state [req]
  {:request req
   :response {:headers {}}
   uri-matching-web-functions []
   })

(defn- webfn-matches-path-or-nil? [path webfn]
  (let [p (get (meta webfn) web/path)]
    (cond
     (nil? p) true                    ; it's a match if it's not specified.
     (fn? p) (true? (p path))
     (string? p) (= p path)
     :otherwise false)))

(defn- webfn-matches-status? [status webfn]
  (let [s (get (meta webfn) web/status)]
    (cond
     (fn? s) (true? (s status))
     (number? s) (= s status)
     ;; if there is no status declared we select the function if the
     ;; status is not an error.
     :otherwise (< status 400))))

(defn- webfn-matches? [path status webfn]
  (and
   (webfn-matches-path-or-nil? path webfn)
   (webfn-matches-status? status webfn)))

(defn get-response [req plugboard]
  (let [[status state] (plugboard/get-status-with-state plugboard
                         (initialize-state req))
        webfns (get state uri-matching-web-functions)]
    (println "webfns is " webfns)
    (if-let [^ContentFunction cf (first webfns)]
      (let [webfn (:webfn cf)
            content-type (:content-type cf) ; TODO: Add a bit of
                                        ; destructuring here.
            headers (merge (get-in state [:response :headers] (get-headers-from-webfn webfn)))
            body (get-body status req webfn content-type)
            ]
        (if (map? body)
          {:status status :headers (merge headers (:headers body)) :body (:body body)}
          {:status status :headers headers :body body})
        )
      ;; If there is no web-fn...
      {:status 404
       :headers []
       :body "<p>No webfunctions match request</p>"}
      )))

;; This creates a handler that can be wrapped in ring middleware.
(defn create-response-handler [plugboard]
  (fn [req]
    (get-response req plugboard)))

;; --------------------------------------------------------------------------------

;; This is almost identical to
;; plugboard.webfunction.response/webfn-matches-path? but doesn't
;; count functions that don't have paths.
(defn webfn-matches-path? [path webfn]
  (let [p (get (meta webfn) web/path)]
    (cond
     (fn? p) (true? (p path))
     (string? p) (= p path)
     :otherwise false)))

(defn get-matching-webfunctions-for-path [path web-namespaces]
  (mapcat
   (fn [web-ns]
     (filter #(webfn-matches-path? path (:webfn %))
             (plugboard.webfunction.plugboards/get-web-functions
              web-ns)))
   web-namespaces))

(defn web-function-resources [namespaces]
  {:init (fn [state]
           (-> state
               (assoc web-namespaces namespaces)
               (assoc plugboard/path (get-in state [:request :route-params "*"]))
               ))
   plugboard/malformed? (fn [state dlg]
                          (println "malformed?")
                          [false (assoc state
                                   uri-matching-web-functions
                                   (get-matching-webfunctions-for-path
                                    (get state plugboard/path)
                                    (get state web-namespaces)))])
   plugboard/resource-exists? (fn [state dlg]
                                [(not (empty? (get state uri-matching-web-functions))) state])})

(defn set-header [state name value]
  (update-in state [:response :headers] (fn [old] (assoc old name value))))

(defn welcome-page [path]
  {plugboard/resource-previously-existed?
   (fn [state dlg] (= \/ (last (get-in state [:request :uri]))))
   plugboard/resource-moved-permanently?
   (fn [state dlg] [true
                   (let [location (str (get-in state [:request :uri]) path)]
                     (set-header state "Location" location))])})

(defn redirect-to-new-resource []
  {plugboard/redirect? (fn [state dlg] true)})

;; The auth string arrives as "Basic user:password" (where user:password is base64 encoded)
(defn compare-secret [expected auth-string]
  (let [actual (second (re-seq #"[\w=]+" auth-string))]
    (= expected actual)))

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
               [false (set-header state "WWW-Authenticate" (format "Basic realm=\"%s\"" realm))]))
         true)))})

(defn negotiate-content [candidates accepts extractor]
  (filter #(not (nil? %))
          (reduce concat
                  (map (fn [accept]
                         (map
                          (fn [^ContentFunction candidate]
                            (when-let [res (conneg/acceptable-type (extractor candidate) accept)]
                              (ContentFunction. (:webfn candidate) res)))
                          candidates))
                       accepts))))

(defn accept []
  {plugboard/acceptable-media-type-available?
   (fn [state dlg]
     (let [accepts (map :type (conneg/sorted-accept (get-in state [:request :headers "accept"])))
           unfiltered-functions (get state uri-matching-web-functions)
           filtered-functions (negotiate-content unfiltered-functions accepts get-content-type-fragment)
           new-state (assoc state uri-matching-web-functions filtered-functions)
           ]
       [(not (empty? filtered-functions)) new-state]
       )
     )})
