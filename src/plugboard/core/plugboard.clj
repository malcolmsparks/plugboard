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

(ns plugboard.core.plugboard)

(def ^{:private true} _path)

(def
 ^{:doc "The relative path of the web resource. Web resources are identified
 by URIs. Web applications are usually responsible for a number of web
 resources, so the path given by the client must often be decoded from the
 URI."}
 path (var _path))


(def transition-map
     {
      [:B1 false] 503
      [:B1 true] :B2
      [:B2 false] :B3
      [:B2 true] 414
      [:B3 false] :B4
      [:B3 true] 400
      [:B4 false] 401
      [:B4 true] :B5
      [:B5 false] :B6
      [:B5 true] 403
      [:B6 false] :B7
      [:B6 true] 200
      [:B7 false] :B8
      [:B7 true] :C7
      [:B8 false] 501
      [:B8 true] 405
      [:C2 false] :D2
      [:C2 true] 412
      [:C7 false] :C2
      [:C7 true] :C8
      [:C8 false] :D9
      [:C8 true] :C9
      [:C9 true] :D9
      [:D2 false] :D4
      [:D4 false] :G4
      [:D4 true] :D5
      [:D5 false] :E5
      [:D5 true] 301
      [:D9 false] :E9
      [:E9 false] :G10
      [:G4 false] 404
      [:G4 true] :H4
      [:G10 false] :H13
      [:H4 false] 404
      [:H4 true] :J4
      [:H13 false] :H15
      [:H13 true] :I13
      [:H15 false] :H18
      [:H18 false] :H19
      [:H19 true] :H20
      [:H20 false] :I21
      [:I13 false] :J13
      [:I21 false] :J22
      [:J4 false] 501
      [:J4 true] :L4
      [:J13 true] :L13
      [:J22 false] :K23
      [:K23 false] :L24
      [:L4 false] 500
      [:L4 true] :M4
      [:L13 false] 500
      [:L13 true] :M13
      [:L24 false] :M24
      [:M2 false] 201
      [:M2 true] 303
      [:M4 false] :M11
      [:M4 true] :M2
      [:M11 true] 200
      [:M13 false] :M11
      [:M13 true] :M14
      [:M14 false] 204
      [:M14 true] 303
      [:M24 true] 200
      })

(defn is-web-method? [& candidates]
  (fn [state dlg]
    (let [method (get-in state [:request :request-method])]
      (not (nil? (some (partial = method)
                       (if (coll? candidates) candidates (list candidates))))))))

(defn header-exists? [header]
  (fn [state dlg]
    (contains? (get-in state [:request :headers]) header)
    ))

(def default-wiring
     {
      :B1 true
      :B2 false
      :B3 false
      :B4 true
      :B5 false
      :B6 (is-web-method? :options)
      :B7 (is-web-method? :delete :get :head :put :post)
      :B8 (is-web-method? :trace :connect)
      :C2 (header-exists? "If-Match")
      :C7 false ; Key junction - does the resource exist?
      :C8 false
      :C9 true
      :D2 (is-web-method? :put)
      :D4 false
      :D9 false
      :E9 false
      :G4 (is-web-method? :post)
      :G10 false
      :H4 false
      :H13 (is-web-method? :post)
      :H15 (is-web-method? :put)
      :H18 (is-web-method? :delete)
      :H19 (is-web-method? :get :head)
      :H20 false
      :I13 false
      :I21 false
      :J13 true
      :J22 false
      :K23 false
      :L13 false
      :L24 false
      :M11 true
      :M13 (fn [state dlg] (string? (get state :location)))
      :M14 true
      :M24 true
      })

;; ------------------------ Construction

(defn map-fn-on-map-vals [m f]
  (zipmap (keys m) (map f (vals m)))
  )

(defn merge-plugboards [& maps]
  (letfn [(cons* [a b]
                 (if (list? a) (cons b a)
                     (list a b)))
          (ensure-list [a] (if (list? a) a (list a)))]
    (map-fn-on-map-vals (apply (partial merge-with cons*) maps) ensure-list)
    ))

;; ------------------------ Flow

(defn- bool? [b]
  (or (true? b) (false? b)))

(defn lookup-decision [plugboard step]
  (let [res (plugboard step)]
    (if (nil? res)
      (throw (Exception. (format "No decision for step: %s" step)))
      res)))

(defn lookup-next [tuple]
  (let [res (transition-map tuple)]
    (if (nil? res)
      (throw (Exception. (format "No transition for tuple: %s" tuple)))
      res)))

; Returns [boolean state] decision
(defn- decide [state t dlg]
  (cond
   (fn? t) (let [res (t state dlg)]
             (if 
                 (vector? res) res
                 [res state])
                 )
   :otherwise [t state]
   ))

; Returns [boolean state]
(defn- decides [state l]
  (decide state (first l) (fn [state] (decides state (rest l))))
  )

(defn ^{:doc "Get the result of the given layer of plug functions. We reverse
        the list so that the first elements of the list override later elements
        during the implementation. Normally we prefer the latter elements of the
        list to override the preceeding elements because that is the convention
        already established by the merge function. Reversing this convention
        would confuse developers already familiar with Clojure conventions."  }
        get-plug-decision [state l]
  (decides state (reverse l))
  )

;; Returns [next-junction new-state]
(defn perform-junction [junction plugboard state]
  (let [[decision new-state] (get-plug-decision state (get plugboard junction))]
    [(lookup-next [junction decision]) new-state])
  )

;; Ultimately returns [status state]
(defn flow-junction [junction plugboard state]
  (let [[next new-state] (perform-junction junction plugboard state)]
    (cond
     (keyword? next) (fn [] (flow-junction next plugboard new-state))
     (integer? next) [next new-state]
     :otherwise (throw (IllegalStateException.)))))

(defn initialize-state [plugboard state]
  (if-let [inits (get plugboard :init)]
    (reduce #(%2 %1) state inits) ; this calls all the init functions, threading the state through each one.
    state
    ))

;; Ultimately returns [status state]
(defn get-status-with-state [plugboard state]
  (let [[status new-state]
        (trampoline flow-junction :B1 plugboard (initialize-state plugboard state))]
    [status new-state]
    ))

(defn get-status [plugboard state]
  (let [[status _] (get-status-with-state plugboard state)]
    status))
