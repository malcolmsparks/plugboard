(ns plugboard.configurations)

(defn is-web-method? [& candidates]
  (fn [state]
    (let [method (get-in state [:request :request-method])]
      (not (nil? (some (partial = method)
                       (if (coll? candidates) candidates (list candidates))))))))

(def default-decision-map
     {
      :B1 true
      :B2 false
      :B3 false
      :B4 true
      :B5 false
      :B6 (is-web-method? :options)
      :B7 (is-web-method? :delete :get :head :put :post)
      :B8 (is-web-method? :trace :connect)
      :C7 false ; Key step - does the resource exist?
      :C8 false
      :C9 true
      :D2 (is-web-method? :put)
      :D4 false
      :D9 false
      :E9 false
      :G4 (is-web-method? :post)
      :G10 false
      :H13 (is-web-method? :post)
      :H15 (is-web-method? :put)
      :H18 (is-web-method? :delete)
      :H19 (is-web-method? :get :head)
      :H20 false
      :I13 false
      :I21 false
      :J13 true
      :J22 false
      :K23 false
      :L13 false ; Key step - must be accepted by a resource appender
      :L24 false
      :M11 true
      :M13 (fn [state] (string? (get state :location)))
      :M14 true
      :M24 true
      })

(defn override-default-decision-map [overrides]
  (merge default-decision-map overrides))
