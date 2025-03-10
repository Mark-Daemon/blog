(ns generator.generate_post_row
  (:require [generator.model :as model]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [generator.util :as util])
  (:import (java.text SimpleDateFormat)
           (java.util TimeZone)))

(defn- generate-post-rows
  "Generates a post row from the post data"
  [file-template post-data]
  {:pre [(s/valid? ::model/post post-data)]}
  (let [{:keys [title date meta excerpt section file-name]} (:post-data post-data)
        formatter (util/create-date-formatter)
        formatted-date (.format formatter date)
        link (str "/" section "/" (str/replace file-name ".md" ".html"))]
    (-> file-template
        (str/replace "{{ post.link }}" link)
        (str/replace "{{ post.title }}" (str/upper-case title))
        (str/replace "{{ post.meta }}" (str/upper-case (str meta " " formatted-date)))
        (str/replace "{{ post.excerpt }}" excerpt))))

(defn- filter-or-apply-posts-count
  "Checks if the filter posts count is greater than zero. If not, remove the entire line. Otherwise insert the count."
  [template posts-count]
  (if (zero? posts-count)
    (str/replace template (util/create-pattern-line-with "{{ posts.count }}") "")
    (str/replace template "{{ posts.count }}" (str posts-count))))

(defn generate-latest-posts
  "Generates the latest posts from the post data"
  [file-content posts-data]
  {:pre [(s/valid? ::model/posts posts-data)]}
  (let [file-template (slurp "data/templates/post-row.html")]
    (-> (->> (reduce #(str %1 (generate-post-rows file-template %2)) "" posts-data)
             (str/replace file-content "{{ posts }}"))
         (-> (filter-or-apply-posts-count (count posts-data))))))
