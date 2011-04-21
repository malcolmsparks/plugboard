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

(def
 ^{:doc "The relative path of the web resource. Web resources are identified
 by URIs. Web applications are usually responsible for a number of web
 resources, so the path given by the client must often be decoded from the
 URI."}
 path ::path)

(def transition-map
     {
      [:B3 false] :C3
      [:B3 true] 200
      [:B4 false] :B3
      [:B4 true] 413
      [:B5 false] :B4
      [:B5 true] 415
      [:B6 false] :B5
      [:B6 true] 501
      [:B7 false] :B6
      [:B7 true] 403
      [:B8 false] 401
      [:B8 true] :B7
      [:B9 false] :B8
      [:B9 true] 400
      [:B10 false] 405
      [:B10 true] :B9
      [:B11 false] :B10
      [:B11 true] 414
      [:B12 false] 501
      [:B12 true] :B11
      [:B13 false] 503
      [:B13 true] :B12
      [:C3 false] :D4
      [:C3 true] :C4
      [:C4 false] 406
      [:C4 true] :D4
      [:D4 true] :D5
      [:D4 false] :E5
      [:D5 false] 406
      [:D5 true] :E5
      [:E5 false] :F6
      [:E5 true] :E6
      [:E6 false] 406
      [:E6 true] :F6
      [:F6 false] :G7
      [:F6 true] :F7
      [:F7 false] 406
      [:F7 true] :G7
      [:G7 false] :H7
      [:G7 true] :G8
      [:G8 false] :H10
      [:G8 true] :G9
      [:G9 false] :G11
      [:G9 true] :H10
      [:G11 false] 412
      [:G11 true] :H10
      [:H7 false] :I7
      [:H7 true] 412
      [:H10 false] :I12
      [:H10 true] :H11
      [:H11 false] :I12
      [:H11 true] :H12
      [:H12 false] :I12
      [:H12 true] 412
      [:I4 false] :P3
      [:I4 true] 301
      [:I7 false] :K7
      [:I7 true] :I4
      [:I12 false] :L13
      [:I12 true] :I13
      [:I13 false] :K13
      [:I13 true] :J18
      [:J18 false] 412
      [:J18 true] 304
      [:K5 false] :L5
      [:K5 true] 301
      [:K7 false] :L7
      [:K7 true] :K5
      [:K13 false] :L13
      [:K13 true] :J18
      [:L5 false] :M5
      [:L5 true] 307
      [:L7 false] 404
      [:L7 true] :M7
      [:L13 false] :M16
      [:L13 true] :L14
      [:L14 false] :M16
      [:L14 true] :L15
      [:L15 false] :L17
      [:L15 true] :M16
      [:L17 false] 304
      [:L17 true] :M16
      [:M5 false] 410
      [:M5 true] :N5
      [:M7 false] 404
      [:M7 true] :N11
      [:M16 false] :N16
      [:M16 true] :M20
      [:M20 false] 202
      [:M20 true] :O20
      [:N5 false] 410
      [:N5 true] :N11
      [:N11 false] :P11
      [:N11 true] 303
      [:N16 false] :O16
      [:N16 true] :N11
      [:O14 false] :N11
      [:O14 true] 409
      [:O16 false] :O18
      [:O16 true] :O14
      [:O18 false] 200
      [:O18 true] 300
      [:O20 false] 204
      [:O20 true] :O18
      [:P3 false] :P11
      [:P3 true] 409
      [:P11 false] :O20
      [:P11 true] 201})

(defn is-web-method? [& candidates]
  (fn [state dlg]
    (let [method (get-in state [:request :request-method])]
      (not (nil? (some (partial = method)
                       (if (coll? candidates) candidates (list candidates))))))))

(defn header-exists? [header]
  (fn [state dlg]
    (contains? (get-in state [:request :headers]) (.toLowerCase header))))

(defn header-is? [header expected]
  (fn [state dlg]
    (= (get-in state [:request :headers header]) expected)))

(def default-wiring
     {:B3 (is-web-method? :options)
      :B4 false
      :B5 false
      :B6 false
      :B7 false
      :B8 true
      :B9 false
      :B10 true
      :B11 false
      :B12 true
      :B13 true
      :C3 (header-exists? "Accept")
      :C4 true
      :D4 (header-exists? "Accept-Language")
      :D5 true
      :E5 (header-exists? "Accept-Charset")
      :E6 true
      :F6 (header-exists? "Accept-Encoding")
      :F7 true
      :G7 false
      :G8 (header-exists? "If-Match")
      :G9 nil
      :G11 nil
      :H7 (header-exists? "If-Match")
      :H10 (header-exists? "If-Unmodified-Since")
      :H11 nil
      :H12 nil
      :I4 false
      :I7 (is-web-method? :put)
      :I12 (header-exists? "If-None-Match") ; TODO: Value cannot be *
      :I13 (header-exists? "If-None-Match") ; TODO: Value must be *
      :J18 nil
      :K5 nil
      :K7 false
      :K13 nil
      :L5 nil
      :L7 (is-web-method? :post)
      :L13 (header-exists? "If-Modified-Since")
      :L14 nil
      :L15 nil
      :L17 nil
      :M5 nil
      :M7 nil
      :M16 (is-web-method? :delete)
      :M20 nil
      :N5 nil
      :N11 nil
      :N16 (is-web-method? :post)
      :O16 (is-web-method? :put)
      :O18 false
      :O20 nil
      :P3 nil
      :P11 nil})

;; ------------------------ Useful aliases

(def START :B13)

(def authorized? :B8)
(def known-method? :B12)
(def request-entity-too-large? :B4)
(def unknown-content-type? :B5)
(def unknown-or-unsupported-content-asterisk-header? :B6)
(def forbidden? :B7)
(def authorized? :B8)
(def malformed? :B9)
(def uri-too-long? :B11)
(def known-method? :B12)
(def available? :B13)
(def accept-exists? :C3)
(def acceptable-media-type-available? :C4)
(def accept-language-exists? :D4)
(def acceptable-language-available? :D5)
(def accept-charset-exists? :E5)
(def acceptable-charset-available :E6)
(def accept-encoding-exists? :F6)
(def acceptable-encoding-available? :F7)
(def resource-exists? :G7)
(def if-match-exists? :G8)
(def it-match-asterisk-exists? :G9)
(def etag-in-if-match? :G11)
(def if-match-asterisk-exists? :H7)
(def if-unmodified-since-exists? :H10)
(def if-unmodified-since-is-valid-date? :H11)
(def last-modified-greater-than-if-unmodified-since? :H12)
(def server-desires-that-the-request-be-applied-to-a-different-URI? :I4)
(def if-none-match? :I12)
(def if-none-match-asterisk? :I13)
(def resource-moved-permanently? :K5)
(def resource-previously-existed? :K7)
(def etag-in-if-none-match? :K13)
(def resource-moved-temporarily? :L5)
(def if-modified-since-exists? :L13)
(def if-modified-since-is-valid-date? :L14)
(def if-modified-since-greater-than-now? :L15)
(def last-modified-greater-than-if-modified-since? :L17)
(def delete-enacted? :M20)
(def redirect? :N11)
(def multiple-representations? :O18)
(def response-includes-an-entity? :O20)
(def new-resource? :P11)

;; Here are aliases for state groups, where the same function is
;; called in multiple states.
(def server-permits-post-to-missing-resource? [:M7 :N5])
(def conflict? [:O14 :P3])

;; ------------------------ Construction

(defn map-fn-on-map-vals [m f]
  (zipmap (keys m) (map f (vals m))))

(defn merge-plugboards [& maps]
  (letfn [
          ;; Similar to cons, but ensures
          (cons* [a b]
                 (if (list? a) (cons b a)
                     (list a b)))
          (ensure-list [a] (if (list? a) a (list a)))
          ;; Any key that happens to be a vector is unrolled into
          ;; individual keys (with the same value for each)
          (unroll-keys [mm] (reduce (fn [m [k v]]
                                      (cond (vector? k) (reduce (fn [m2 k2] (assoc m2 k2 v)) m k)
                                            :otherwise (assoc m k v)))
                                    {} mm))]
    (->
     (partial merge-with cons*)         ; Create the merging function
     (apply (map unroll-keys maps))          ; Call the merging function to
                                        ; all the maps (we expand each
                                        ; map first)
     (map-fn-on-map-vals ensure-list))))

;; ------------------------ Flow

(defn- bool? [b]
  (or (true? b) (false? b)))

(defn lookup-decision [plugboard step]
  (let [res (get plugboard step)]
    (if (nil? res)
      (throw (Exception. (format "No decision for step: %s" step)))
      res)))

(defn lookup-next [tuple]
  (let [res (get transition-map tuple)]
    (if (nil? res)
      (throw (Exception. (format "No transition for tuple: %s" tuple)))
      res)))

;; Returns [boolean state] decision
(defn- decide [state f dlg]
  (cond
   (fn? f) (let [res (f state dlg)]
             (if
                 (vector? res) res
                 [res state]))
   :otherwise [f state]))

;; Returns [boolean state]
(defn- decides [state stack]
  (decide state (first stack) (fn [state] (decides state (rest stack)))))

(defn ^{:doc "Get the result of the given layer of plug functions. We reverse
        the list so that the first elements of the list override later elements
        during the implementation. Normally we prefer the latter elements of the
        list to override the preceeding elements because that is the convention
        already established by the merge function. Reversing this convention
        would confuse developers already familiar with Clojure conventions."  }
  get-plug-decision [state stack]
  (decides state (reverse stack)))

;; Returns [next-junction new-state]
(defn perform-junction [junction plugboard state]
  (let [[decision new-state] (get-plug-decision state (get plugboard junction))]

    (cond
     (nil? decision) (throw (Exception. (format "No decision at junction %s" junction)))
     (integer? decision) [decision new-state]
     :otherwise
     (let [next (lookup-next [junction decision])]
;;      (println (format "%s -> %s -> %s" junction decision next))
       [next new-state]))))

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
    state))

;; Ultimately returns [status state]
;; B13 is the start state.
(defn get-status-with-state [plugboard state]
  (let [[status new-state]
        (trampoline flow-junction START plugboard (initialize-state plugboard state))]
    [status new-state]))

(defn get-status [plugboard state]
  (let [[status _] (get-status-with-state plugboard state)]
    status))
