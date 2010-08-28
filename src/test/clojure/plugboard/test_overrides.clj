(ns plugboard.test-overrides
  (:use
   clojure.test
   plugboard.status
   )
  (:require [plugboard.core :as plugboard])
  )

(defn insert-path [state]
  [false (merge {plugboard/path "a/b/index.html"} state)]
  )

(defn resource-exists [state]
  (if (= "a/b/index.html" (get state plugboard/path))
    [true state]
    [false state]
  ))

(deftest path-insertion
  (is (= 404 (get-status {} {:request {:request-method :get}})))
  (is (= 200 (get-status {:B3 insert-path :C7 resource-exists} {:request {:request-method :get}})))
    )



