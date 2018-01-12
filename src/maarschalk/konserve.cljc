(ns maarschalk.konserve
  (:require [konserve.protocols :refer [PStoreSerializer -serialize -deserialize]]
            #?@(:clj [[clojure.data.fressian :as fress]
                      [maarschalk.fressian :as m]
                      [incognito.fressian :refer [incognito-read-handlers
                                                  incognito-write-handlers]]])

            [incognito.edn :refer [read-string-safe]])
  #?(:clj (:import [java.io FileOutputStream FileInputStream DataInputStream DataOutputStream]
                   [org.fressian.handlers WriteHandler ReadHandler])))

#?(:clj
   (defrecord FressianSerializer []
     PStoreSerializer
     (-deserialize [_ read-handlers bytes]
       (fress/read bytes
                   :handlers (-> (merge fress/clojure-read-handlers
                                        {m/joda-tag m/joda-reader
                                         m/instant-tag m/instant-reader}
                                        (incognito-read-handlers read-handlers))
                                 fress/associative-lookup)))

     (-serialize [_ bytes write-handlers val]
       (let [w (fress/create-writer bytes :handlers (-> (merge
                                                         fress/clojure-write-handlers
                                                         {org.joda.time.DateTime {m/joda-tag m/joda-writer}
                                                          java.time.Instant {m/instant-tag m/instant-writer}}
                                                         (incognito-write-handlers write-handlers))
                                                        fress/associative-lookup
                                                        fress/inheritance-lookup))]
         (fress/write-object w val)))))

#?(:clj
   (defn fressian-serializer []
     (map->FressianSerializer {})))



