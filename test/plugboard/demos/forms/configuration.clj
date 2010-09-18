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

(ns plugboard.demos.forms.configuration
  (:require
   (plugboard.webfunction plugboards response)
   plugboard.core.plugboard
   plugboard.demos.forms.webfunctions
   )
  )

(defn appender [state]
  "new-resource.html"
  )

(defn create-plugboard []
  (plugboard.core.plugboard/merge-plugboards

   ;; We start with the defaults.
   plugboard.core.plugboard/default-wiring

   ;; Then add the logic that treats functions which have particular metadata
   ;; as web resources.
   (plugboard.webfunction.plugboards/web-function-resources
    (map find-ns ['plugboard.demos.forms.webfunctions]))

   ;; Here we add a simple plugboard combinator that adds welcome page
   ;; behaviour when the uri ends in a slash.
   (plugboard.webfunction.plugboards/welcome-page "index.html")

   ;; Here we add the appender which creates the new resource and returns its location.
   (plugboard.webfunction.plugboards/redirecting-appender appender)

   ))
