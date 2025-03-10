(ns generator.generate_navigation
  (:require [generator.model :as model]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [generator.util :as util]))

(defn- generate-navigation-item
  "Generates a navigation item from the section data"
  [template section link]
  (-> (str/replace template "{{ navigation.title }}" (str/upper-case section))
      (str/replace "{{ navigation.link }}" link)))

(defn- create-section-link-path
  "Creates a section link path from the section data"
  [home-section section]
  (if (= home-section section)
    (str util/blog-path "index.html")
    (str util/blog-path section "/index.html")))

(defn- generate-navigation-content
  "Generates the navigation bar content from the blog data"
  [blog-data]
  {:pre [(s/valid? ::model/blog blog-data)]}
  (let [template (slurp "data/templates/navigation-item.html")
        home-section (:home-section blog-data)
        sections (:sections blog-data)]
    (reduce
      #(str %1 (generate-navigation-item template %2 (create-section-link-path home-section %2))) "" sections)))

(defn generate-navigation
  "Generates the navigation bar from the blog data"
  [file-content blog-data]
  {:pre [(s/valid? ::model/blog blog-data)]}
  (str/replace file-content "{{ navigation }}" (generate-navigation-content blog-data)))
