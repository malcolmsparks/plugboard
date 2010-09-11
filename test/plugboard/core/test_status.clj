(ns plugboard.core.test-status
  (:use
   clojure.test
   plugboard.core.plugboard
   )
  )

(deftest test-is-method
  (is ((is-web-method? :put) {:request {:request-method :put}} nil))
  (is (not ((is-web-method? :put) {:request {:request-method :post}} nil)))
  (is ((is-web-method? :get :head) {:request {:request-method :get}} nil))
  (is (not ((is-web-method? :get :head) {:request {:request-method :options}} nil)))
  )

(deftest test-header-exists
  (is ((header-exists? "If-Match") {:request {:headers {"If-Match" "*"}}} nil))
  (is (not ((header-exists? "If-Match") {:request {:headers {"Dummy" "true"}}} nil)))
  )

(deftest test-get-status
  (is (= 200 (get-status (merge-plugboards default-decision-map) {:request {:request-method :options}})))
  (is (= 404 (get-status (merge-plugboards default-decision-map) {:request {:request-method :get}})))
  )

;; TODO: Would be better to use a record to return the [status state] in.
;; TODO: Not sure about the above TODO - perhaps better that we don't use records.

(deftest test-can-override-resource-exists
  (is (= 200 (get-status (merge-plugboards default-decision-map {:C7 true})
                         {:request {:request-method :get}}))))

