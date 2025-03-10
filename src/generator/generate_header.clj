(ns generator.generate_header
  (:require [generator.model :as model]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn generate-header
  "Generates the header from the blog data"
  [file-content blog-data section name]
  {:pre [(s/valid? ::model/blog blog-data)]}
  (let [{:keys [short long terminal]} (:blog-title blog-data)
        page-name (if (str/starts-with? name "index") "" (str "/" (str/upper-case name)))
        path (if (= section (:home-section blog-data))
               "MAIN"
               (str (str/upper-case section) (str/upper-case page-name)))]
    (-> file-content
        (str/replace "{{ title.short }}" short)
        (str/replace "{{ title.long }}" long)
        (str/replace "{{ title.terminal }}" terminal)
        (str/replace "{{ header-path }}" path))))
