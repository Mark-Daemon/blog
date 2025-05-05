(ns generator.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [generator.parse-input :as parse-input]
            [generator.generate_rss :as rss]
            [generator.template_model :as template_model]
            [generator.input_model :as model]
            [generator.templater :as templater]
            [generator.generate-search :as search]
            [generator.generate-static :as static]
            [generator.generate-clojurescript :as cljs]
            [generator.util :as util])
  (:gen-class))

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

(defn generate-blog-content!
  [blog-data posts-data]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/posts posts-data)]}
  (let [blog-template (template_model/parse-input-to-template-data blog-data posts-data)
        sections (:sections blog-template)
        index-template (slurp "data/templates/index.html")
        post-template (slurp "data/templates/post-index.html")]
    (run! #(generate-section blog-template % index-template post-template) sections)
    (search/generate_search_index! blog-template)))

(defn generate-site!
  "Generates the complete site from templates and data"
  []
  (let [blog-data (parse-input/process-blog-data)
        posts-data (parse-input/process-posts-data)
        static-task #(static/copy-static-files!)
        blog-task #(generate-blog-content! blog-data posts-data)
        rss-task #(rss/generate-rss-feed! blog-data posts-data)
        compile-task #(cljs/compile-and-copy-clojurescript!)
        _ (static/clear-public-directory!)
        tasks (map util/to-future [static-task blog-task rss-task compile-task])]
    (util/resolve-futures tasks)))

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
      (.printStackTrace e))))

(defn -main
  "Generate blog from templates and yaml data"
  [& args]
  (let [start-time (System/currentTimeMillis)]
    (generate-site!)
    (shutdown-agents)
    (println "Site generated in" (/ (- (System/currentTimeMillis) start-time) 1000.0) "seconds")))
