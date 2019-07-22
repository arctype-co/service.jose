(ns arctype.service.jose
  (:import 
    [com.nimbusds.jose EncryptionMethod JWEAlgorithm JWEHeader$Builder JWEObject Payload]
    [com.nimbusds.jose.crypto DirectEncrypter]
    [javax.crypto KeyGenerator SecretKey]
    [javax.crypto.spec SecretKeySpec]
    [org.apache.commons.codec.binary Hex])
  (:require
    [clojure.tools.logging :as log]
    [arctype.service.protocol :refer :all]
    [schema.core :as S]
    [sundbry.resource :as resource]))

(def Config
  {:default-key S/Str
   :keys {S/Keyword S/Str}})

(def ^:private default-config
  {})

(S/defn generate-key :- S/Str
  []
  (let [keygen (KeyGenerator/getInstance "AES")]
    (.init keygen 128)
    (let [sk (.generateKey keygen)]
      (Hex/encodeHexString (.getEncoded sk)))))

(S/defn encrypt :- S/Str
  ([this body :- S/Str] (encrypt this (:default-key this) body))
  ([{:keys [secret-keys]}
    key-id :- S/Str
    body :- S/Str] 
   (let [secret-key (or (get secret-keys key-id) (throw (ex-info "Secret key not found" {:key-id key-id})))
         header (-> (JWEHeader$Builder. JWEAlgorithm/DIR EncryptionMethod/A128GCM)
                    (.keyID key-id)
                    (.build))
         obj (JWEObject. header (Payload. body))]
     (.encrypt obj (DirectEncrypter. secret-key))
     (.serialize obj))))

(S/defn decrypt :- S/Any
  [this cipher-text :- S/Str]
  (throw (UnsupportedOperationException. "decrypt not implemented")))

(defrecord JoseService [default-key secret-keys]
  PLifecycle

  (start [this]
    (log/info {:message "Starting JOSE service" :default-key default-key})
    this)

  (stop [this]
    (log/info {:message "Stopping JOSE service"})
    this)

  )

(defn- load-secret-key
  [^String hex-str]
  (let [sk-bytes (.decode (Hex.) (.getBytes hex-str))]
    (SecretKeySpec. sk-bytes "AES")))

(S/defn create
  [resource-name
   config :- Config]
  (let [config (merge default-config config)
        secret-keys (->> (:keys config)
                         (map (fn [[key-id key-str]]
                                [(name key-id) (load-secret-key key-str)]))
                         (into {}))]
    (resource/make-resource
      (map->JoseService {:default-key (:default-key config)
                         :secret-keys secret-keys})
      resource-name)))
