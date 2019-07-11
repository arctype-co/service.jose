(defproject arctype/service.jose "0.1.0-SNAPSHOT"
  :dependencies 
  [[org.clojure/clojure "1.9.0"]
   [arctype/service "0.1.0-SNAPSHOT"]
   [net.minidev/json-smart "2.3"]
   [com.nimbusds/nimbus-jose-jwt "7.4"
    :exclusions [net.minidev/json-smart]]])
