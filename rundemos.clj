(ns rundemos
  (:require
   [swank.swank :as swank]
   ring.adapter.jetty
   [plugboard.configurations :as pc]
   (webfunction plugboards response)
   basic.webfunctions   
   ))

(defn application-handler [req]
  (webfunction.response/get-response
   req
   (merge pc/default-decision-map
          (webfunction.plugboards/web-function-resources
           (map find-ns ['basic.webfunctions]))

          ;; Here we add a simple plugboard combinator that adds welcome
          ;; page behaviour when the uri ends in a slash.
          (webfunction.plugboards/welcome-page "index.html") 

          )
   ))



(ring.adapter.jetty/run-jetty
 application-handler
 {:join? false
  :port 8082}
 )

(swank.swank/start-repl)
