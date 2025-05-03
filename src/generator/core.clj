(ns generator.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [generator.parse-input :as parse-input]
            [generator.generate_rss :as rss]
            [generator.template_model :as template_model]
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

(defn generate-site!
  "Generates the complete site from templates and data"
  []
  (let [blog-data (parse-input/process-blog-data)
        posts-data (parse-input/process-posts-data)]
    (static/clear-public-directory!)
    (static/copy-static-files!)
    (cljs/compile-and-copy-clojurescript!)
    (let [blog-template (template_model/parse-input-to-template-data blog-data posts-data)
          sections (:sections blog-template)
          index-template (slurp "data/templates/index.html")
          post-template (slurp "data/templates/post-index.html")]
      (run! #(generate-section blog-template % index-template post-template) sections)
      (search/generate_search_index! blog-template))
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
      (.printStackTrace e))))

(defn -main
  "Generate blog from templates and yaml data"
  [& args]
  (generate-site!))
