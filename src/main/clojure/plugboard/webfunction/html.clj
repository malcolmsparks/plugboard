(ns plugboard.webfunction.html)

(defmulti table type)
(defmethod table clojure.lang.IPersistentMap
  [m]
  (vec
   (concat
    [:table {:border 2}]
    (map (fn [[k v]]
           (vector :tr [:td (str k)] [:td (str v)]))
         (seq m))))
  )

