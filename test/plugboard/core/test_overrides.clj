(ns plugboard.core.test-overrides
  (:use
   clojure.test
   )
  (:require
   [plugboard.core.plugboard :as plugboard]
   )
  )

(defn insert-path [state dlg]
  [false (merge {plugboard/path "a/b/index.html"} state)]
  )

(defn resource-exists [state dlg]
  (if (= "a/b/index.html" (get state plugboard/path))
    [true state]
    [false state]
  ))

(deftest path-insertion
  (is (= 404 (plugboard/get-status
              (plugboard/merge-plugboards
               plugboard/default-decision-map)
              {:request {:request-method :get}})))
  (is (= 200 (plugboard/get-status
              (plugboard/merge-plugboards
               plugboard/default-decision-map
               {:B3 insert-path :C7 resource-exists})
              {:request {:request-method :get}})))
    )



