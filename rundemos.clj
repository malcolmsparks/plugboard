(ns rundemos
  (:require
   [swank.swank :as swank]
   ring.adapter.jetty
   plugboard.demos.main
   ))

(ring.adapter.jetty/run-jetty
 plugboard.demos.main/create-application-handler
 {:join? false
  :port 8082}
 )

(swank.swank/start-repl)
