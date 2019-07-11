(ns arctype.service.jose
  (:require
    [clojure.tools.logging :as log]
    [arctype.service.protocol :refer :all]
    [schema.core :as S]
    [sundbry.resource :as resource]))

(def Config
  {:keys {S/Keyword S/Str}})

(def ^:private default-config
  {})

(defrecord JoseService []
  PLifecycle

  (start [this]
    (log/info {:message "Starting JOSE service"})
    )

  (stop [this]
    (log/info {:message "Stopping JOSE service"})
    )

  )

(S/defn create
  [resource-name
   config :- Config]
  (let [config (rmerge default-config config)]
    (resource/make-resource
      (map->JoseService)
      resource-name)))
