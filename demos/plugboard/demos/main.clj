(ns plugboard.demos.main
  (:require
   [plugboard.core.configurations :as pc]
   (plugboard.webfunction plugboards response)
   plugboard.demos.basic.webfunctions   
   ))

(defn create-application-handler [req]
  (plugboard.webfunction.response/get-response
   req
   (merge pc/default-decision-map
          (plugboard.webfunction.plugboards/web-function-resources
           (map find-ns ['plugboard.demos.basic.webfunctions]))

          ;; Here we add a simple plugboard combinator that adds welcome
          ;; page behaviour when the uri ends in a slash.
          (plugboard.webfunction.plugboards/welcome-page "index.html") 

          )
   ))