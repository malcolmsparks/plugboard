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
  (:use
   clojure.tools.trace
   clojure.pprint)
  (:require [plugboard.webfunction.webfunction :as web]
            [clojure.data.codec.base64 :as base64]
            [plugboard.core.plugboard :as plugboard]
            [plugboard.core.conneg :as conneg]))

;; Replace with hiccup
(defn html [& body] nil)

(defrecord ContentFunction [webfn content-type])

(defn is-web-namespace? [ns]
  (= (find-ns 'plugboard.webfunction.webfunction) ns))

(defn is-web-function? [f]
  (-> f
      meta
      keys
      ((partial filter keyword?))
      ((partial map namespace))
      ((partial filter (fn [x] (not (nil? x)))))
      ((partial map symbol))
      ((partial map find-ns))
      ((partial filter is-web-namespace?))
      empty?
      not))

(defn get-web-functions [ns]
  (map (fn [webfn] (ContentFunction. webfn (get (meta webfn) web/content-type)))
       (filter is-web-function? (map second (ns-publics ns)))))

;; --------------------------------------------------------------------------------

(defn
  ^{:doc "Get the content type from the web function metadata"}
  get-content-type-fragment [^ContentFunction cf]
  (:type (conneg/accept-fragment (or (get (meta (:webfn cf)) web/content-type) "*/*"))))

(defn get-headers-from-webfn [webfn]
  (if-let [ct (get (meta webfn) web/content-type)]
    {"Content-Type" ct}
    {}))

(defn get-body [status state request webfn content-type]
  (if (not (nil? webfn))
    (with-bindings {(var web/*web-context*)
                    {:status status :state state :request request :meta (meta webfn) :content-type content-type}}
      (webfn))))

(defn initialize-state [req]
  {:request req
   :response {:headers {}}
   ::uri-matching-web-functions []})

(defn- webfn-matches-path-or-nil? [path webfn]
  (let [p (get (meta webfn) web/path)]
    (cond
     (nil? p) true                    ; it's a match if it's not specified.
     (fn? p) (true? (p path))
     (string? p) (= p path)
     :otherwise false)))

(defn webfn-matches-status? [status webfn]
  (let [s (get (meta webfn) web/status)]
    (cond
     (fn? s) (true? (s status))
     (number? s) (= s status)
     ;; if there is no status declared we select the function if the
     ;; status is not an error.
     :otherwise (< status 400))))

(defn by-status [status]
  (fn [^ContentFunction cf]
    (webfn-matches-status? status (:webfn cf))))

;; TODO: Split this large function up in the next major release.
(defn get-response
  [req plugboard & {:keys [error-hook] :or {error-hook identity}}]
  "Drive the plugboard and use the result to select the view"
  (let [[status state] (plugboard/get-status-with-state plugboard
                         (initialize-state req))
        webfns (or (get state ::content-type-filtered-uri-matching-web-functions)
                   (get state ::uri-matching-web-functions))
        matching-webfns (filter (by-status status) webfns)]
    (if (:immediate-exit state)
      {:status status :headers (get-in state [:response :headers]) :body ""}
      (if-let [^ContentFunction cf (first matching-webfns)]
        (let [webfn (:webfn cf)
              content-type (:content-type cf) ; TODO: Add a bit of
                                        ; destructuring here.
              headers (merge (get-headers-from-webfn webfn)
                             (get-in state [:response :headers]))]
          (try
            (let [body (get-body status state req webfn content-type)]
              (if (map? body)
                {:status (or (:status body) status) :headers (merge headers (:headers body)) :body (:body body)}
                {:status status :headers headers :body body}))
            (catch Exception e
              (let [status 500
                    matching-webfns (filter (by-status status) (get state ::uri-matching-web-functions))]
                (if-let [^ContentFunction cf (first matching-webfns)]
                  ;; TODO: Test this, release it, use the error handler to log the support ticket, and add the support ticket id
                  ;; into the state
                  (let [body (get-body status (error-hook (assoc state :exception e)) req (:webfn cf) content-type)]
                    (if (map? body)
                      {:status (or (:status body) status) :headers (merge headers (:headers body)) :body (:body body)}
                      {:status status :headers headers :body body}))
                  ;; Otherwise just rethrow
                  (throw e))))))
        ;; If there is no web-fn...
        {:status status
         :headers (assoc (get-in state [:response :headers]) "Content-Type" "text/html")
         :body (with-out-str
                 (html [:body [:p "No web-functions match request"]
                                               [:hr] [:p {:style "font-size: smaller"} "Served by plugboard"]]))}))))

;; This creates a handler that can be wrapped in ring middleware.
(defn create-response-handler [plugboard & opts]
  (fn [req]
    (apply get-response (concat (list req plugboard) opts))))

;; --------------------------------------------------------------------------------

(defn webfn-matches-path? [path webfn]
  (let [p (get (meta webfn) web/path)]
    (cond
     (nil? p) true ; accept anything if nil, it could be a handler for
                                        ; all paths
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

(defn is-resource [^ContentFunction cf]
  (let [md (meta (:webfn cf))
        res (get md web/resource)]
    (cond
     (fn? res) (res)
     (nil? res) (contains? md web/path)
     (true? res) true
     (false? res) false
     :otherwise false)))

(defn web-function-resources [namespaces]
  (if (not (every? #(not (nil? %)) namespaces))
    (throw (Exception. "If any of the namespaces given to this function are nil, this will cause errors and confusion during requests. Please fix this error before proceeding."))
    {:init (fn [state]
             (-> state
                 (assoc ::web-namespaces namespaces)
                 (assoc plugboard/path (get-in state [:request :route-params :*]))))
     plugboard/malformed? (fn [state dlg]
                            [false (assoc state
                                     ::uri-matching-web-functions
                                     (get-matching-webfunctions-for-path
                                      (get state plugboard/path)
                                      (get state ::web-namespaces)))])
     plugboard/resource-exists? (fn [state dlg]
                                  (let [result
                                        (not (empty? (filter is-resource (get state ::uri-matching-web-functions))))]
                                    [result state]))}))

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
   (let [encoded (base64/encode (.getBytes (str user ":" password)))]
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
  {

   plugboard/acceptable-media-type-available?
   (fn [state dlg]
     (let [accepts (map :type
                        (conneg/sorted-accept
                         (get-in state [:request :headers "accept"])))
           unfiltered-functions (get state ::uri-matching-web-functions)]
       (if (empty? unfiltered-functions)
         ;; There are no matching functions.
         ;; If we had some, and filtered out all the unacceptable media
         ;; types, then we would return a 406.
         ;; However, if there are no resources at this stage then we
         ;; need to carry on to the resource-exists logic, so we have
         ;; fire a true return state from C4
         ;; TODO: Why not rename web-functions to 'representations'?
         true

         (let [filtered-functions (negotiate-content unfiltered-functions accepts get-content-type-fragment)
               new-state (assoc state ::content-type-filtered-uri-matching-web-functions filtered-functions)]
           [(not (empty? filtered-functions)) new-state]))))})
