(ns plugboard.core.test-status
  (:use
   clojure.test
   plugboard.core.plugboard
   plugboard.core.configurations
   )
  )

(deftest test-is-method
  (is ((is-web-method? :put) {:request {:request-method :put}}))
  (is (not ((is-web-method? :put) {:request {:request-method :post}})))
  (is ((is-web-method? :get :head) {:request {:request-method :get}}))
  (is (not ((is-web-method? :get :head) {:request {:request-method :options}})))
  )

(deftest test-header-exists
  (is ((header-exists? "If-Match") {:request {:headers {"If-Match" "*"}}}))
  (is (not ((header-exists? "If-Match") {:request {:headers {"Dummy" "true"}}})))
  )

(deftest test-get-status
  (is (= 200 (get-status default-decision-map {:request {:request-method :options}})))
  (is (= 404 (get-status default-decision-map {:request {:request-method :get}})))
  )

;; TODO: Would be better to use a record to return the [status state] in.

(deftest test-can-override-resource-exists
  (is (= 200 (get-status (override-default-decision-map {:C7 true}) {:request {:request-method :get}}))))

