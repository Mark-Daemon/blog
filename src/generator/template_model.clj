(ns generator.template_model
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [generator.input_model :as input]
            [markdown.core :as md]))

(s/def ::meta (s/or :nil nil? :meta string?))
(s/def ::content string?)
(s/def ::date inst?)
(s/def ::section-name string?)
(s/def ::extra (s/or :nil nil? :extra any?))
(s/def ::link string?)
(s/def ::is-section-index boolean?)
(s/def ::TemplatePost (s/keys :req-un [::is-section-index ::section-name ::file-name ::title ::content ::date]
                              :opt-un [::extra ::meta]))
(defrecord TemplatePost [file-name title meta content date is-section-index section-name link extra])

(s/def ::title string?)
(s/def ::file-name string?)
(s/def ::is-home boolean?)
(s/def ::posts (s/coll-of ::TemplatePost))
(s/def ::section-post (s/or :nil nil? :post ::TemplatePost))
(s/def ::TemplateSection (s/keys :req-un [::is-home ::title ::section-post ::posts ::link]))
(defrecord TemplateSection [is-home title section-post posts link])

(s/def ::long-title string?)
(s/def ::short-title string?)
(s/def ::terminal-title string?)
(s/def ::link string?)
(s/def ::sections (s/coll-of ::TemplateSection))
(s/def ::TemplateBlog (s/keys :req-un [::short-title ::long-title ::terminal-title ::link ::sections]))
(defrecord TemplateBlog [short-title long-title terminal-title link sections])

(defn- normalize-ordered-maps
  "Recursively converts all #ordered/map instances to standard Clojure maps."
  [data]
  (cond
    (instance? flatland.ordered.map.OrderedMap data) (into {} (map normalize-ordered-maps data))
    (map? data) (into {} (map (fn [[k v]] [k (normalize-ordered-maps v)]) data))
    (coll? data) (mapv normalize-ordered-maps data)
    :else data))

(defn- parse-post
  [blog post]
  {:pre  [(s/valid? ::input/post post)
          (s/valid? ::input/blog blog)]
   :post [(s/valid? ::TemplatePost %)]}
  (let [is-section-index (= (:title post) "index")
        link (str (:link blog) "/" (:section post) "/" (str/replace (:file-name post) ".md" ".html"))
        left-over-values (normalize-ordered-maps (dissoc post :title :date :section :excerpt :file-name :content :meta))
        adjusted-post (-> post (assoc :is-section-index is-section-index
                                      :link link
                                      :section-name (:section post)
                                      :extra left-over-values
                                      :content  (md/md-to-html-string (:content post)))
                          (dissoc :section))]
    (map->TemplatePost adjusted-post)))

(defn parse-section
  [blog-data section all-posts]
  {:pre  [(s/valid? ::input/blog blog-data)
          (s/valid? ::input/section section)
          (s/valid? ::posts all-posts)]
   :post [(s/valid? ::TemplateSection %)]}
  (let [is-home (= section (:home-section blog-data))
        section-post (first (filter #(and (= (:section-name %) section) (:is-section-index %)) all-posts))
        posts (filter #(or (and is-home (not (:is-section-index %)))
                           (and (not (:is-section-index %)) (= (:section-name %) section))) all-posts)
        sorted-posts (sort-by #(.getTime (:date %)) > posts)
        link (str (:link blog-data) (if is-home "" (str "/" section)) "/index.html")]
    (->TemplateSection is-home section section-post sorted-posts link)))

(defn parse-blog
  [blog-data sections]
  {:pre  [(s/valid? ::input/blog blog-data)
          (s/valid? ::sections sections)]
   :post [(s/valid? ::TemplateBlog %)]}
  (let [{:keys [short long terminal]} (:blog-title blog-data)]
    (->TemplateBlog short long terminal (:link blog-data) sections)))

(defn parse-input-to-template-data
  [blog-data posts]
  {:pre  [(s/valid? ::input/blog blog-data)
          (s/valid? ::input/posts posts)]
   :post [(s/valid? ::TemplateBlog %)]}
  (let [template-posts (map #(parse-post blog-data %) posts)
        template-sections (map #(parse-section blog-data % template-posts) (:sections blog-data))]
    (parse-blog blog-data template-sections)))
