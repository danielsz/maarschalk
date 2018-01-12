(ns maarschalk.fressian
  (:require [clojure.data.fressian :as fressian]
            [clj-time.format :as f])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
           [org.fressian.handlers WriteHandler ReadHandler]
           [java.time Instant]))

(def joda-tag "joda")

(def joda-writer
  (reify WriteHandler
    (write [_ writer instant]
      (.writeTag    writer joda-tag 1)
      (.writeObject writer (f/unparse (f/formatters :date-time) instant)))))

(def joda-reader
  (reify ReadHandler
    (read [_ reader tag component-count]
      (f/parse (f/formatters :date-time) (.readObject reader)))))

(def instant-tag "instant")

(def instant-writer
  (reify WriteHandler
    (write [_ writer instant]
      (.writeTag    writer instant-tag 1)
      (.writeObject writer (str instant)))))

(def instant-reader
  (reify ReadHandler
    (read [_ reader tag component-count]
      (Instant/parse (.readObject reader)))))

(def my-write-handlers
  (-> (merge {org.joda.time.DateTime {joda-tag joda-writer}
              java.time.Instant {instant-tag instant-writer}}
             fressian/clojure-write-handlers)
      fressian/associative-lookup
      fressian/inheritance-lookup))

(def my-read-handlers
  (-> (merge {joda-tag joda-reader instant-tag instant-reader} fressian/clojure-read-handlers)
      fressian/associative-lookup))


