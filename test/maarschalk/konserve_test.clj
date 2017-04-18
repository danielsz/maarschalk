(ns maarschalk.konserve-test
  (:refer-clojure :exclude [get-in update-in assoc-in dissoc exists?])
  (:require [maarschalk.konserve :as m]
            [clojure.test :refer :all]
            [clojure.core.async :refer [<!!]]
            [konserve.core :refer :all]
            [konserve.filestore :refer [new-fs-store delete-store list-keys]]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(deftest serializers-test
  (testing "Test the custom fressian serializers functionality."
    (let [folder "/tmp/konserve-fs-test"
          _ (delete-store folder)
          store (<!! (new-fs-store folder :serializer (m/fressian-serializer)))]
      (is (= (<!! (get-in store [:foo]))
             nil))
      (<!! (assoc-in store [:foo] (t/now)))
      (is (= (type (<!! (get-in store [:foo])))
             org.joda.time.DateTime))
      (is (= (<!! (list-keys store))
             #{[:foo]}))
      (<!! (dissoc store :foo))
      (is (= (<!! (get-in store [:foo]))
             nil))
      (is (= (<!! (list-keys store))
             #{})))))
