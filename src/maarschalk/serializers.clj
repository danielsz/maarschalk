(ns maarschalk.serializers
 (:require [clj-time.format :as f])
 (:import [java.time Instant]
          [org.joda.time DateTime]
          [java.io Writer]))

(defn- joda-instant->reader-str [d]
  (str "#maarschalk/joda-inst \"" (f/unparse (f/formatters :date-time) d) \"))

(defn reader-str->joda-instant [s]
  (f/parse (f/formatters :date-time) s))

(defn reader-str->java8-instant [s]
  (Instant/parse s))

(defmethod print-dup org.joda.time.DateTime [^DateTime d ^Writer out]
  (.write out (joda-instant->reader-str d)))

(defmethod print-method org.joda.time.DateTime [^DateTime d ^Writer out]
  (.write out (joda-instant->reader-str d)))

(defmethod print-dup java.time.Instant [^Instant d ^Writer out]
  (.write out (format "#maarschalk/java8-inst \"%s\"" d)))

(defmethod print-method java.time.Instant [^Instant d ^Writer out]
  (.write out (format "#maarschalk/java8-inst \"%s\"" d)))
