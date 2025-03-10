(ns generator.generate_post
  (:require [generator.model :as model]
            [clojure.spec.alpha :as s]
            [generator.util :as util]
            [generator.generate_header :as header]
            [generator.generate_navigation :as navigation]
            [markdown.core :as md]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [generator.generate_performance :as performance]))

(defn- apply-or-filter-meta
  "Filters the meta if it is empty"
  [template meta]
  (if (empty? meta)
    (str/replace template (util/create-pattern-line-with "{{ post.meta }}") "")
    (str/replace template "{{ post.meta }}" meta)))

(defn generate-post-content
  "Generates the post content from the post data"
  [title meta post-content]
  {:pre [(s/valid? string? post-content)
         (s/valid? string? title)
         (or (nil? meta) (s/valid? string? meta))]}
  (-> (slurp "data/templates/post-content.html")
      (str/replace "{{ post.title }}" (str/upper-case title))
      (apply-or-filter-meta meta)
      (str/replace "{{ post.content }}" (md/md-to-html-string post-content))))

(defn apply-or-filter-performance
  ""
  [template post-data]
  (let [performance (:performance (:post-data post-data))
        date (:date (:post-data post-data))]
    (if (nil? performance)
      (str/replace template (util/create-pattern-line-with "{{ performance }}") "")
      (let [{:keys [mental physical productivity]} performance]
        (str/replace template "{{ performance }}"
                     (performance/generate-performance-report mental physical productivity date))))))

(defn- generate-post-page
  "Generates a post page from the post data"
  [blog-data post-data]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/post post-data)]}
  (let [{:keys [title meta section file-name]} (:post-data post-data)
        content (:content post-data)
        name-without-extension (str/replace file-name ".md" "")
        to-store-name (str util/publish-folder section "/" (str/replace file-name ".md" ".html"))
        date-formatter (util/create-date-formatter)
        meta-with-date (str/upper-case (if (nil? meta)
                                         (.format date-formatter (:date (:post-data post-data)))
                                         (str meta " " (.format date-formatter (:date (:post-data post-data))))))
        template (slurp "data/templates/post-index.html")]
    (io/make-parents (io/file to-store-name))
    (-> template
        (header/generate-header blog-data section name-without-extension)
        (navigation/generate-navigation blog-data)
        (str/replace "{{ section.post }}" (generate-post-content title meta-with-date content))
        (apply-or-filter-performance post-data)
        (->> (spit to-store-name)))))

(defn generate-post-pages!
  "Generates the post pages from the post data"
  [blog-data post-data]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/posts post-data)]}
  (->> post-data
       (filter #(not= (:title (:post-data %)) "index.md"))
       (run! #(generate-post-page blog-data %))))
