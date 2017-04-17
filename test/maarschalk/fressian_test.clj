(ns maarschalk.fressian-test
  (:require
   [clojure.data.fressian :as fressian]
   [maarschalk.fressian :as m]
   [clj-time.core :as t]
   [clojure.test :refer :all])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
           [org.fressian.handlers WriteHandler ReadHandler]))

(deftest joda []
  (let [baos (ByteArrayOutputStream.)
        writer (fressian/create-writer baos :handlers m/my-write-handlers)
        now (t/now)]
    (fressian/write-object writer now)
    (let [bais (ByteArrayInputStream. (.toByteArray baos))
          reader (fressian/create-reader bais :handlers m/my-read-handlers)
          out (fressian/read-object reader)]
      (is (= now out)))))
