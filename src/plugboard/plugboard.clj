(ns plugboard.plugboard)

(def ^{:private true} _path)

(def
 ^{:doc "The relative path of the web resource. Web resources are identified
 by URIs. Web applications are usually responsible for a number of web
 resources, so the path given by the client must often be decoded from the
 URI."}
 path (var _path))


(def transition-map
     {
      [:B1 false] 503
      [:B1 true] :B2
      [:B2 false] :B3
      [:B2 true] 414
      [:B3 false] :B4
      [:B3 true] 400
      [:B4 false] 401
      [:B4 true] :B5
      [:B5 false] :B6
      [:B5 true] 403
      [:B6 false] :B7
      [:B6 true] 200
      [:B7 true] :C7
      [:C7 false] :D2
      [:C7 true] :C8
      [:C8 false] :D9
      [:C8 true] :C9
      [:C9 true] :D9
      [:D2 false] :D4
      [:D4 false] :G4
      [:D9 false] :E9
      [:E9 false] :G10
      [:G4 false] 404
      [:G10 false] :H13
      [:H13 false] :H15
      [:H13 true] :I13
      [:H15 false] :H18
      [:H18 false] :H19
      [:H19 true] :H20
      [:H20 false] :I21
      [:I13 false] :J13
      [:I21 false] :J22
      [:J13 true] :L13
      [:J22 false] :K23
      [:K23 false] :L24
      [:L13 false] 500
      [:L13 true] :M13
      [:L24 false] :M24
      [:M11 true] 200
      [:M13 false] :M11
      [:M13 true] :M14
      [:M14 false] 204
      [:M14 true] 303
      [:M24 true] 200
      })

(defn- bool? [b]
  (or (true? b) (false? b)))

;; TODO: Rewrite to check existence with 'contains?'
;; Decisions can return a boolean or a [boolean new-state-members]. (TODO: Document this)
(defn lookup-decision [decision-map step]
  (let [res (decision-map step)]
    (if (nil? res)
      (throw (Exception. (format "No decision for step: %s" step)))
      res)))

(defn lookup-next [tuple]
  (let [res (transition-map tuple)]
    (if (nil? res)
      (throw (Exception. (format "No transition for tuple: %s" tuple)))
      res)))

(defn add-transition-record [state step1 decision-result step2]
  (let [existing-trace (:status-trace state) tuple [step1 decision-result step2]]
    (assoc state :status-trace
           (if (nil? existing-trace) (list tuple)
               (conj existing-trace tuple))))
)

;; Returns [next-step new-state]
;; TODO: Simplify.
(defn perform-step [step decision-map state]
  (let [decision (lookup-decision decision-map step)]
    (cond
     (fn? decision)
     (let [decision-result (decision state)]
       (cond
        (bool? decision-result)
        (let [next (lookup-next [step decision-result])]
          [next (add-transition-record state step decision-result next)])

        (vector? decision-result)
        (let [next (lookup-next [step (first decision-result)])]
          [next (add-transition-record (merge state (second decision-result)) step decision-result next)]
          )

        :otherwise (throw (IllegalStateException. (format "Step %s. Function %s must result in a boolean" step decision-result)))))

     (bool? decision)
     (let [next (lookup-next [step decision])] [next (add-transition-record state step decision next)])

     :otherwise (throw (IllegalStateException.)))))

;; Ultimately returns [status state]
(defn flow-step [step decision-map state]
  (let [[next new-state] (perform-step step decision-map state)]
    (cond
     (keyword? next) (fn [] (flow-step next decision-map new-state))
     (integer? next) [next new-state]
     :otherwise (throw (IllegalStateException.)))))

;; Ultimately returns [status state]
(defn get-status-with-state [decision-map state]
  (let [reverse-transition-trace (fn [state]
                 (if (contains? state :status-trace)
                   (assoc state :status-trace (reverse (:status-trace state)))
                   state))
        [status new-state]
        (trampoline flow-step :B1 decision-map (reverse-transition-trace state))
        ]
    [status (reverse-transition-trace new-state)]
    ))

(defn get-status [decision-map state]
  (let [[status state] (get-status-with-state decision-map state)]
    status))
