(ns maarschalk.serializers
  (:require [#?(:clj clj-time.format :cljs cljs-time.format) :as f]
            #?@(:cljs [[cljs.reader :as reader]
                       [cljs-time.coerce :as coerce]
                       [ankha.core :as ankha]]))
  #?(:clj (:import [org.joda.time DateTime]
                   [java.time Instant]
                   [java.io Writer]
                   [org.bson.types ObjectId]))
  #?(:cljs (:import [goog.date DateTime])))

(defn- joda-instant->reader-str [d]
  (str "#maarschalk/joda-inst \"" (f/unparse (f/formatters :date-time) d) \"))

(defn reader-str->joda-instant [s]
  (f/parse (f/formatters :date-time) s))

(defn reader-str->mongo-object-id [s]
  #?(:clj (ObjectId. s)
     :cljs s))

(defn reader-str->java8-instant [s]
  #?(:clj (Instant/parse s)
     :cljs (.fromRfc822String DateTime s)))

#?(:clj (do
          (defmethod print-dup org.joda.time.DateTime [^DateTime d ^Writer out]
            (.write out (joda-instant->reader-str d)))

          (defmethod print-method org.joda.time.DateTime [^DateTime d ^Writer out]
            (.write out (joda-instant->reader-str d)))

          (defmethod print-dup java.time.Instant [^Instant d ^Writer out]
            (.write out (format "#maarschalk/java8-inst \"%s\"" d)))

          (defmethod print-method java.time.Instant [^Instant d ^Writer out]
            (.write out (format "#maarschalk/java8-inst \"%s\"" d)))

          (defmethod print-dup org.bson.types.ObjectId [^ObjectId d ^Writer out]
            (.write out (format "#maarschalk/mongo-object-id \"%s\"" d)))

          (defmethod print-method org.bson.types.ObjectId [^ObjectId d ^Writer out]
            (.write out (format "#maarschalk/mongo-object-id \"%s\"" d)))))

#?(:cljs (do
           (cljs.reader/register-tag-parser! 'maarschalk/joda-inst reader-str->joda-instant)
           (cljs.reader/register-tag-parser! 'maarschalk/java8-inst reader-str->java8-instant)
           (cljs.reader/register-tag-parser! 'maarschalk/mongo-object-id reader-str->mongo-object-id)
           
           (extend-protocol IPrintWithWriter
             goog.date.DateTime
             (-pr-writer [d out opts]
               (-write out (joda-instant->reader-str d))))

           (extend-protocol ankha/IInspect
             goog.date.DateTime
             (-inspect [this]
               (ankha/literal "maarschalk/joda-inst" (f/unparse (f/formatters :date-time) this))))))
