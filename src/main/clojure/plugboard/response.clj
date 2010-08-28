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

(ns plugboard.response
  (:use hiccup.core)
  )

(def background-color "#c0c0c0")

(def status-messages {
                      200 "OK"
                      })

(defn- fit-content [content]
  (cond
   (and (vector? content) (keyword? (first content))) [:body content]
   (coll? content) (apply vector (cons :body content))
   :otherwise content))

;; TODO: Have callers supply templaters.
(defn- wrap-in-template [content]
  {:headers {"Content-Type" "text/html"}
   :body
   (reduce str
           (interpose "\n"
                      (list
                       "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                       "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
                       (html
                        [:html {:xmlns "http://www.w3.org/1999/xhtml"}
                         [:head [:style (format "body { background: %s }" background-color)]]
                         (fit-content content)
                         ]))))})

(defn default-view-functions []
  {
   303 (fn [status state]
         (wrap-in-template
          [:p
           "Thank you for your response. Please go to "
           [:a {:href (get-in state [:request :uri])} (get-in state [:request :uri])]]))
   404 (fn [status state] (wrap-in-template "Not found"))
   })

(defn create-view [status state view-function-overrides]
  (let [view-fn
        (or (get view-function-overrides status)
            (get view-function-overrides :default)
            (get (default-view-functions) status)
            )]
    (cond
     (nil? view-fn) (wrap-in-template [:p status " " (get status-messages status)])
     :otherwise (view-fn status state)
     )))
