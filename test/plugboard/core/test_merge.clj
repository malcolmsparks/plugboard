(ns plugboard.core.test-merge
  (:use clojure.test
        plugboard.core.plugboard)
  )

(defn- get-example [] (merge-plugboards
                      {:B1 false :B2 100 :B3 (fn [state dlg] true) :B4 false}
                      {:B1 true :B2 (fn [state dlg] (dlg (assoc state :zip "wire"))) :B3 (fn [state dlg] false)}
                      ))

(defn- simple-test [state]
  (decides {:foo "foo" :bar "bar"} (reverse (get (get-example) state))))

(deftest test-decides
  (is (= true (first (simple-test :B1))))
  (is (= 100 (first (simple-test :B2))))
  (is (= false (first (simple-test :B3))))
  )
