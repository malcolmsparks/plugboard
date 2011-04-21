(ns sample.main
  (:use compojure.core)
  (:require
   [swank.swank :as swank]
   ring.adapter.jetty
   plugboard.core.plugboard
   plugboard.webfunction.plugboards
   sample.representations
   ))

(ring.adapter.jetty/run-jetty
;; plugboard.demos.main/create-application-handler
 (routes
  (GET "/*" [] (plugboard.core.plugboard/merge-plugboards

   ;; We start with the defaults.
   plugboard.core.plugboard/default-wiring

   ;; Then add the logic that treats functions which have particular metadata
   ;; as web resources.
   (plugboard.webfunction.plugboards/web-function-resources
    (map find-ns ['sample.representations]))

   ;; Here we add a simple plugboard that adds welcome page
   ;; behaviour when the uri ends in a slash.
   (plugboard.webfunction.plugboards/welcome-page "index.html") 

   )))
 {:join? false
  :port 8082}
 )

(swank.swank/start-repl)
