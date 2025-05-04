(ns generator.parse-input
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clj-yaml.core :as yaml]
            [generator.input_model :as model]))

(defn process-post-data
  "Consumes the post data file and returns it as a data object"
  [file]
  (let [[raw-post-data raw-post-content end-content] (str/split (slurp file) #"---\n")
        yaml-post-data (yaml/parse-string raw-post-data)
        post-data (assoc yaml-post-data :file-name (.getName file))]
    (model/create-post
      (-> post-data
          (assoc :content raw-post-content)
          (assoc :sub-content end-content)))))

(defn process-posts-data
  "Collects all post data and content and returns it as a list of data objects"
  []
  {:post [(s/valid? ::model/posts %)]}
  (let [post-files (filter #(str/ends-with? (.getName %) ".md") (file-seq (io/file "data/posts")))
        post-data (map process-post-data post-files)]
    post-data))

(defn process-blog-data
  "Processes the blog data file and returns it as a data object"
  []
  (let [blog-data (yaml/parse-string (slurp "data/blog.yaml"))]
    (model/create-blog blog-data)))
