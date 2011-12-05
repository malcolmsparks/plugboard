(ns plugboard.util
  (:require
   [clj-http.client :as http]
   ))

;; Copied from clojure.contrib.with-ns
(defmacro with-ns
  "Evaluates body in another namespace.  ns is either a namespace
  object or a symbol.  This makes it possible to define functions in
  namespaces other than the current one."
  [ns & body]
  `(binding [*ns* (the-ns ~ns)]
     ~@(map (fn [form] `(eval '~form)) body)))

;; Copied from clojure.contrib.with-ns
(defmacro with-temp-ns
  "Evaluates body in an anonymous namespace, which is then immediately
  removed.  The temporary namespace will 'refer' clojure.core."
  [& body]
  `(do (create-ns 'sym#)
       (let [result# (with-ns 'sym#
                      (clojure.core/refer-clojure)
                      ~@body)]
         (remove-ns 'sym#)
         result#)))

(defn encode-form
  [m]
  (reduce str
          (interpose "&"
                     (map (fn [[k v]] (str
                                      (clj-http.util/url-encode k)
                                      "="
                                      (clj-http.util/url-encode v)))
                          m))))

(defn post-form [url m]
  (http/post url
             {:content-type "application/x-www-form-urlencoded"
              :body (encode-form m)
              }))


