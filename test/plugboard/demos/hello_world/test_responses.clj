(ns plugboard.demos.hello-world.test-responses
  (:use clojure.test compojure.core)
  (:require
   ring.adapter.jetty
   plugboard.demos.hello-world.configuration
   [clj-http.client :as client]
   [clojure.xml :as xml]
   [clojure.zip :as zip]
   [clojure.contrib.zip-filter.xml :as zf]
   )
  )

(def port 8083)

(defn create-handler [plugboard]
  (fn [req]
    (plugboard.webfunction.response/get-response req plugboard)
    ))

(defn run-jetty []
  (ring.adapter.jetty/run-jetty
   ;; plugboard.demos.main/create-application-handler
   (defroutes main-routes
     (GET "/hello-world/*" []
          (create-handler (plugboard.demos.hello-world.configuration/create-plugboard))))
   {:join? false :port port}
   ))

(defn jetty [f]
  (let [jetty (run-jetty)]
    (try 
      (f)
      (finally
       (.stop jetty)))))


(use-fixtures :once jetty)

(defn body-zip [response]
  (zip/xml-zip (xml/parse
                (org.xml.sax.InputSource.
                 (java.io.StringReader. (:body response))))))

(deftest test-demo
  (let [response (client/get (format "http://localhost:%d/hello-world/" port))
        doc (body-zip response)]
    (= 200 (get response :status))
    (is (= "Hello World!" (zf/xml1-> doc :body :h1 zf/text)))
    )
  )
