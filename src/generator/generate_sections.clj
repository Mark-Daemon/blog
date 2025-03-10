(ns generator.generate_sections
  (:require [generator.model :as model]
            [clojure.spec.alpha :as s]
            [generator.generate_index :as index]))

(defn- generate-section
  "Generates a section from the section data"
  [blog-data posts-data section]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/posts posts-data)
         (s/valid? ::model/section section)]}
  (index/generate-index! blog-data posts-data section (str section "/index.html")))

(defn- filter-posts-by-section
  "Filters the posts by section"
  [posts-data section]
  (filter #(= section (:section (:post-data %))) posts-data))

(defn generate-sections!
  "Generates all section index files from the blog data"
  [blog-data posts-data]
  {:pre [(s/valid? ::model/blog blog-data)
         (s/valid? ::model/posts posts-data)]}
  (let [home-section (:home-section blog-data)
        sections (filter #(not= home-section %) (:sections blog-data))]
    (run! #(generate-section blog-data (filter-posts-by-section posts-data %) %) sections)))
