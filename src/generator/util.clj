(ns generator.util
  (:import (java.text SimpleDateFormat)
           (java.util TimeZone)))

(def publish-folder "docs/blog/")

(defn create-date-formatter
  "Create the style of date formatter to be consistently used across the app"
  []
  (doto (SimpleDateFormat. "yyyy-MM-dd")
    (.setTimeZone (TimeZone/getTimeZone "UTC"))))

(defn create-RFC-822-date-formatter
  "Create the style of date formatter to be consistently used across the app"
  []
  (doto (SimpleDateFormat. "EEE, dd MMM yyyy HH:mm:ss Z")
    (.setTimeZone (TimeZone/getTimeZone "UTC"))))
