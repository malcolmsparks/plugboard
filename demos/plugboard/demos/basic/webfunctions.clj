(ns plugboard.demos.basic.webfunctions
  (:use
   plugboard.webfunction.context
   )
  (:require
   [plugboard.webfunction.webfunction :as web]
   [hiccup.core :as hiccup]
   )
  )

(defn ^{web/uri "/index.html"
        web/content-type "text/html"
        :title "Demo: Basic"}
  index-html []
  (hiccup/html
   [:h1 (get-meta :title)]
   [:p "Welcome to the demo site"]))

