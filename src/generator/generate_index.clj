(ns generator.generate_index
  (:require [clojure.java.io :as io]
            [generator.model :as model]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [generator.generate_header :as header]
            [generator.generate_navigation :as navigation]
            [generator.generate_post_row :as post-row]
            [generator.generate_post :as post]
            [generator.generate_performance :as performance]
            [generator.util :as util]))

(defn- apply-section-post
  "Applies the section post to the template"
  [template section-post]
  {:pre [(s/valid? string? template)
         (or (nil? section-post) (s/valid? ::model/post section-post))]}
  (if (nil? section-post)
    (str/replace template "{{ section.post }}" "")
    (let [post-data (:post-data section-post)
          section (:section post-data)
          meta (:meta post-data)
          post-content (:content section-post)]
      (->> (post/generate-post-content section meta post-content)
           (str/replace template "{{ section.post }}")))))

(defn- find-and-apply-section-post
  "Generates a section post from the post data"
  [template blog-data posts-data section]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/posts posts-data)]}
  (let [section-post (first (filter #(and (= (:title (:post-data %)) "index.md")
                                          (= (:section (:post-data %)) section)) posts-data))]
    (if (nil? section-post)
      (apply-section-post template nil)
      (apply-section-post template section-post))))

(defn- find-and-apply-perf-report-for-home
  "Generates a performance report for the home page"
  [template blog-data section posts-data]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/section section)
         (s/valid? ::model/posts posts-data)]}
  (if (= (:home-section blog-data) section)
    (let [posts-with-performance (filter #(not (nil? (:performance (:post-data %)))) posts-data)
          sorted-posts (sort-by #(.getTime (:date (:post-data %))) > posts-with-performance)
          latest-post (first sorted-posts)
          last-post (:post-data latest-post)
          date (:date last-post)
          {:keys [mental physical productivity]} (:performance last-post)]
      (str/replace template "{{ performance }}"
                   (performance/generate-performance-report mental physical productivity date)))
    (str/replace template (util/create-pattern-line-with "{{ performance }}") "")))

(defn- extract-landing-posts-data
  "Extracts the first 5 posts from the post data"
  [posts-data is-home]
  {:pre [(s/valid? ::model/posts posts-data)]}
  (->> posts-data
       (filter #(not= (:title (:post-data %)) "index.md"))
       (sort-by #(.getTime (:date (:post-data %))) >)
       (take (if is-home
               util/landing-recent-posts-count
               (count posts-data)))))

(defn- generate-index-page!
  "Generates index.html from template by replacing placeholders with blog data"
  [blog-data posts-data section output-name]
  {:pre [(s/valid? ::model/blog blog-data)]}
  (let [index-template (slurp "data/templates/index.html")
        is-home (= section (:home-section blog-data))
        landing-posts-data (extract-landing-posts-data posts-data is-home)]
    (-> index-template
        (header/generate-header blog-data section (last (str/split output-name #"/")))
        (navigation/generate-navigation blog-data)
        (post-row/generate-latest-posts landing-posts-data)
        (find-and-apply-section-post blog-data posts-data section)
        (find-and-apply-perf-report-for-home blog-data section posts-data)
        (->> (spit (str util/publish-folder output-name))))))

(defn generate-index!
  "Generates the index page from blog data and post data"
  [blog-data posts-data section output-name]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/posts posts-data)
         (s/valid? string? output-name)]}
  (if (.contains output-name "/")
    (io/make-parents (io/file (str util/publish-folder output-name))))
  (generate-index-page! blog-data posts-data section output-name))
