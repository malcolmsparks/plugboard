(ns plugboard.core)

(def
 ^{:doc "The relative path of the web resource. Web resources are identified
 by URIs. Web applications are usually responsible for a number of web
 resources, so the path given by the client must often be decoded from the
 URI."}
 path (atom :path))
