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

(ns plugboard.rundemos
  (:require
   [swank.swank :as swank]
   ring.adapter.jetty
   plugboard.demos.main
   ))

(ring.adapter.jetty/run-jetty
;; plugboard.demos.main/create-application-handler
 plugboard.demos.main/main-routes
 {:join? false
  :port 8082}
 )

(swank.swank/start-repl)
