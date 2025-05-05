(ns generator.util
  (:import (java.text SimpleDateFormat)
           (java.util TimeZone))
  (:require [clojure.java.io :as io]))

(def publish-folder "docs/")

(defn delete-directory
  "Recursively delete a directory and its contents."
  [raw-dir]
  (let [dir (if (string? raw-dir)
              (io/file raw-dir)
              raw-dir)]
    (when (.exists dir)
     (doseq [file (.listFiles dir)]
       (if (.isDirectory file)
         (delete-directory file)
         (io/delete-file file true)))
     (io/delete-file dir true))))

(defn to-future
  [f]
  (future
    (try
       (f)
       (catch Exception e
         (.printStackTrace e)
          (throw e)))))

(defn resolve-futures
  [tasks]
  (doseq [f tasks]
    (deref f 60000 {:timeout true})))

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
