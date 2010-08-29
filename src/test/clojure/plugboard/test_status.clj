(ns plugboard.test-status
  (:use
   clojure.test
   plugboard.core
   )
  )

(deftest test-is-method
  (is ((is-method? :put) {:request {:request-method :put}}))
  (is (not ((is-method? :put) {:request {:request-method :post}})))
  (is ((is-method? :get :head) {:request {:request-method :get}}))
  (is (not ((is-method? :get :head) {:request {:request-method :options}})))
  )

(deftest test-get-status
  (is (= 200 (get-status {} {:request {:request-method :options}})))
  (is (= 404 (get-status {} {:request {:request-method :get}})))
  )

;; TODO: Would be better to use a record to return the [status state] in.

(deftest test-can-override-resource-exists
  (is (= 200 (get-status {:C7 true} {:request {:request-method :get}}))))

