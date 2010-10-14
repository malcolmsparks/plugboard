(ns plugboard.util
  (:require
   [clj-http.client :as http]
))

(defn encode-form
  [m]
  (reduce str
          (interpose "&"
                     (map (fn [[k v]] (str
                                      (clj-http.util/url-encode k)
                                      "="
                                      (clj-http.util/url-encode v)
                                      ))
                          m
                          ))))

(defn post-form [url m]
  (http/post
   url
   {
    :content-type "application/x-www-form-urlencoded"
    :body (encode-form m)
    })
  )


