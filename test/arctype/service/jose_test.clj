(ns arctype.service.jose-test
  (:require
    [clojure.test :refer :all]
    [arctype.service.jose :as jose]))

(defn- new-test-config
  []
  {:default-key "test-k-0"
   :keys {:test-k-0 (jose/generate-key)}})

(deftest test-symmetry
  (let [svc (jose/create :jose (new-test-config))
        data "Hello world"
        encrypted (jose/encrypt svc data)]
    (is (string? encrypted))
    
    (let [clear (jose/decrypt svc encrypted)]
      (is (string? clear))
      (is (= data clear)))))
