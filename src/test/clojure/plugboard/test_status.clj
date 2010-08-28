(ns plugboard.test-status
  (:use
   clojure.test
   plugboard.status
   )
  )

(deftest test-is-method
  (is ((is-method? :put) {:request {:request-method :put}}))
  (is (not ((is-method? :put) {:request {:request-method :post}})))
  (is ((is-method? :get :head) {:request {:request-method :get}}))
  (is (not ((is-method? :get :head) {:request {:request-method :options}})))
  )

(deftest test-get-status
  (is (= 200 (first (get-status {} {:request {:request-method :options}}))))
  (is (= 200 (first (get-status {} {:request {:request-method :get}}))))
  )
