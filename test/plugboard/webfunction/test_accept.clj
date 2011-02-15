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

(ns plugboard.webfunction.test-accept
  (:use
   clojure.test
   clojure.contrib.with-ns
   plugboard.webfunction.plugboards
   [plugboard.core.conneg :as conneg])
  (:import plugboard.webfunction.plugboards.ContentFunction))

(deftest test-conneg
  (letfn [(fmt [p] (apply format (cons "%s/%s" p)))]
    (are
     [expected candidates accept]
     (= expected
        (first
         (map fmt
              (map :content-type
                   (negotiate-content
                    (map #(ContentFunction. nil (:type (conneg/accept-fragment %)))
                         candidates)
                    (map :type (conneg/sorted-accept accept))
                    :content-type)))))

     "text/html"
     ["text/html" "text/xml"]
     "text/*, image/png"

     "image/png"
     ["application/xml" "image/gif" "image/png"]
     "text/*, image/png"

     "image/png"
     ["application/xml" "image/gif" "image/png"]
     "image/png"

     nil
     ["application/xml" "image/gif"]
     "image/png"

     ;; From RFC-2616
     "audio/basic"
     ["audio/ogg" "audio/basic" "audio/mp4"]
     "audio/*; q=0.2, audio/basic"

     "text/x-c"
     ["text/x-c" "text/x-dvi" "text/plain"]
     "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c"

     "text/x-dvi"
     ["text/x-dvi" "text/plain"]
     "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c"

     "text/plain"
     ["text/plain"]
     "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c"
     )))
