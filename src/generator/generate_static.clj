(ns generator.generate-static
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [generator.util :as util]))

(defn copy-static-files!
  "Copies static files to the output directory"
  []
  (doseq [file (filter #(.isFile %) (file-seq (io/file "data")))]
    (if (or (.contains (.getPath file) "templates/styles")
            (.contains (.getPath file) "templates/js")
            (.contains (.getName file) "favicon")
            (.contains (.getPath file) "image/")
            (and (.contains (.getPath file) "posts/")
                 (or
                   (str/ends-with? (.getName file) ".html")
                   (str/ends-with? (.getName file) ".js")
                   (str/ends-with? (.getName file) ".css"))))
      (do
        (let [parent_dir (cond
                           (.contains (.getPath file) "posts/") #"posts/"
                           (.contains (.getPath file) "image/") #"data/"
                           :else #"templates/")
              file-path (.getPath file)
              target-path (str util/publish-folder (second (str/split file-path parent_dir)))
              target-file (io/file target-path)]
          (io/make-parents target-file)
          (io/copy file target-file))))))

(defn clear-public-directory!
  "Clears the public directory"
  []
  (let [public (io/file util/publish-folder)]
    (when (.exists public)
      (doseq [file (reverse (file-seq public))]
        (.delete file)))))
