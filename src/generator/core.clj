(ns generator.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clj-yaml.core :as yaml]
            [generator.model :as model]
            [generator.generate_index :as index]
            [generator.generate_sections :as sections]
            [generator.generate_post :as post]
            [generator.generate_rss :as rss]
            [generator.util :as util])
  (:gen-class))

(defn process-post-data
  "Consumes the post data file and returns it as a data object"
  [file]
  (let [[raw-post-data raw-post-content] (str/split (slurp file) #"---\n")
        yaml-post-data (yaml/parse-string raw-post-data)
        post-data (assoc yaml-post-data :file-name (.getName file))]
    (model/create-post post-data raw-post-content)))

(defn process-posts-data
  "Collects all post data and content and returns it as a list of data objects"
  []
  {:post [(s/valid? ::model/posts %)]}
  (let [post-files (file-seq (io/file "data/posts"))
        post-data (map process-post-data (filter #(.isFile %) post-files))]
    post-data))

(defn copy-static-files
  "Copies static files to the output directory"
  []
  (doseq [file (filter #(.isFile %) (file-seq (io/file "data/templates")))]
    (if (or (.contains (.getPath file) "templates/styles")
            (.contains (.getPath file) "templates/js")
            (.contains (.getName file) "favicon"))
      (do
        (let [file-path (.getPath file)
              target-path (str util/publish-folder (second (str/split file-path #"templates/")))
              target-file (io/file target-path)]
          (io/make-parents target-file)
          (io/copy file target-file))))))

(defn clear-public-directory
  "Clears the public directory"
  []
  (let [public (io/file util/publish-folder)]
    (when (.exists public)
      (doseq [file (reverse (file-seq public))]
        (.delete file)))))

(defn generate-site
  "Generates the complete site from templates and data"
  []
  (let [blog-data (model/create-blog (yaml/parse-string (slurp "data/blog.yaml")))
        posts-data (process-posts-data)]
    (clear-public-directory)
    (copy-static-files)
    (index/generate-index! blog-data posts-data (:home-section blog-data) "index.html")
    (sections/generate-sections! blog-data posts-data)
    (post/generate-post-pages! blog-data posts-data)
    (rss/generate-rss-feed! blog-data posts-data)))

;; (do
;;   (require '[generator.core :as core] :reload-all)
;;   (core/reload!))
(defn reload!
  "Reload function for use in REPL during development"
  []
  (try
    (println "Regenerating site...")
    (generate-site)
    (println "Site regenerated.")
    (catch Exception e
      (println "Error generating site:")
      (println (.getMessage e)))))

(defn -main
  "Generate blog from templates and yaml data"
  [& args]
  (generate-site))
