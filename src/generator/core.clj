(ns generator.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clj-yaml.core :as yaml]
            [generator.input_model :as model]
            [generator.generate_rss :as rss]
            [generator.template_model :as template_model]
            [generator.templater :as templater]
            [generator.util :as util])
  (:gen-class))

(defn process-post-data
  "Consumes the post data file and returns it as a data object"
  [file]
  (let [[raw-post-data raw-post-content] (str/split (slurp file) #"---\n")
        yaml-post-data (yaml/parse-string raw-post-data)
        post-data (assoc yaml-post-data :file-name (.getName file))]
    (model/create-post (assoc post-data :content raw-post-content))))

(defn process-posts-data
  "Collects all post data and content and returns it as a list of data objects"
  []
  {:post [(s/valid? ::model/posts %)]}
  (let [post-files (filter #(str/ends-with? (.getName %) ".md") (file-seq (io/file "data/posts")))
        post-data (map process-post-data post-files)]
    post-data))

(defn copy-static-files!
  "Copies static files to the output directory"
  []
  (doseq [file (filter #(.isFile %) (file-seq (io/file "data")))]
    (if (or (.contains (.getPath file) "templates/styles")
            (.contains (.getPath file) "templates/js")
            (.contains (.getName file) "favicon")
            (.contains (.getPath file) "image/")
            (and (.contains (.getPath file) "posts/") (str/ends-with? (.getName file) ".html")))
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

(defn generate-section
  [blog section index-template post-template]
  {:pre [(s/valid? ::template_model/TemplateBlog blog)
         (s/valid? ::template_model/TemplateSection section)]}
  (let [
        section-index (first (filter #(:is-section-index %) (:posts section)))
        section-posts (filter #(not (:is-section-index %)) (:posts section))
        section-path (str util/publish-folder (if (:is-home section) "" (str (:title section) "/")))
        index-path (str section-path "index.html")]
    (io/make-parents (io/file index-path))
    (spit index-path (templater/eval-template index-template blog section section-index))
    (if (not (:is-home section))
      (doseq [post section-posts]
        (spit (str section-path "/" (str/replace (:file-name post) ".md" ".html"))
              (templater/eval-template post-template blog section post))))))

(defn generate-site!
  "Generates the complete site from templates and data"
  []
  (let [blog-data (model/create-blog (yaml/parse-string (slurp "data/blog.yaml")))
        posts-data (process-posts-data)]
    (clear-public-directory!)
    (copy-static-files!)
    (let [blog-template (template_model/parse-input-to-template-data blog-data posts-data)
          sections (:sections blog-template)
          index-template (slurp "data/templates/index.html")
          post-template (slurp "data/templates/post-index.html")]
      (run! #(generate-section blog-template % index-template post-template) sections))
    (rss/generate-rss-feed! blog-data posts-data)))

;; (require '[generator.core :as core] :reload-all)
;; (core/reload!)
(defn reload!
  "Reload function for use in REPL during development"
  []
  (try
    (println "Regenerating site...")
    (generate-site!)
    (println "Site regenerated.")
    (catch Exception e
      (println "Error generating site:")
      (println (.getMessage e)))))

(defn -main
  "Generate blog from templates and yaml data"
  [& args]
  (generate-site!))
