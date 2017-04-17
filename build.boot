(set-env! :source-paths   #{"src" "test"}
          :resource-paths #{"src"}
          :dependencies '[[adzerk/boot-test "1.2.0" :scope "test"]
                          [org.clojure/data.fressian "0.2.1"]
                          [clj-time "0.13.0"]
                          [io.replikativ/incognito "0.2.1"]])

(require '[adzerk.boot-test :refer :all])

(task-options!
 push {:repo-map {:url "https://clojars.org/repo/"}}
 pom {:project 'org.danielsz/maarschalk
      :version "0.1.0-SNAPSHOT"
      :scm {:name "git"
            :url "https://github.com/danielsz/maarschalk"}})

(deftask build
  []
  (comp (pom) (jar) (install)))

(deftask push-release
  []
  (comp
   (build)
   (push)))

(deftask run-test
  []
  (comp
   (watch)
   (test)))
