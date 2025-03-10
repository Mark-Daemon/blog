(ns generator.util
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  (:import (java.text SimpleDateFormat)
           (java.util TimeZone)))

(def publish-folder "docs/blog/")
(def blog-path "/blog/")
(def landing-recent-posts-count 3)

(defn- escape-special-chars
  "Escapes special characters in the given string"
  [string]
  (-> string
      (str/replace "{" "\\{")
      (str/replace "}" "\\}")))

(defn create-pattern-line-with
  "Creates a pattern line with the given pattern"
  [string]
  (re-pattern (str "(?m)^.*" (escape-special-chars string) ".*$\n?")))

(defn create-date-formatter
  "Create the style of date formatter to be consistently used across the app"
  []
  (doto (SimpleDateFormat. "yyyy-MM-dd")
    (.setTimeZone (TimeZone/getTimeZone "UTC"))))
