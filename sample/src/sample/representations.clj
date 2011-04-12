(ns sample.representations
  (:require
   [plugboard.webfunction.webfunction :as web]
   []))

(defn ^{web/path "index.html"
        web/content-type "text/html"
        :title "Hello World!"}
  index-html []
  (hiccup/html
   [:html
    [:head [:title (web/get-meta :title)]]
    [:body
     [:h1 (web/get-meta :title)]
     [:p "Congratulations. Your plugboard project is working."]]]))
