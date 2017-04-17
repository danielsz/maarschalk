(ns maarschalk.fressian
  (:require [clojure.data.fressian :as fressian]
            [clj-time.format :as f])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
           [org.fressian.handlers WriteHandler ReadHandler]))

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

(def my-write-handlers
  (-> (merge {org.joda.time.DateTime {joda-tag joda-writer}}
             fressian/clojure-write-handlers)
      fressian/associative-lookup
      fressian/inheritance-lookup))

(def my-read-handlers
  (-> (merge {joda-tag joda-reader} fressian/clojure-read-handlers)
      fressian/associative-lookup))

;; (defn demo []
;;   (let [baos (ByteArrayOutputStream.)
;;         ;; create writer with my-write-handlers
;;         writer (fressian/create-writer baos :handlers my-write-handlers)
;;         now (t/now)]
;;     (fressian/write-object writer now)
;;     (let [bais (ByteArrayInputStream. (.toByteArray baos))
;;           ;; create reader with my-read-handlers
;;           reader (fressian/create-reader bais :handlers my-read-handlers)
;;           out (fressian/read-object reader)]
;;       (assert (= now out)))))
;; yup, got the same jodatime!

