(ns generator.model
  (:require [clojure.spec.alpha :as s]))

;; Basic specs for fields
(s/def ::title string?)
(s/def ::date inst?)
(s/def ::content string?)
(s/def ::section string?)
(s/def ::excerpt string?)
(s/def ::file-name string?)

;; Perf report specs
(s/def ::mental number?)
(s/def ::physical number?)
(s/def ::productivity number?)
(s/def ::perf-report (s/keys :req-un [::mental ::physical ::productivity]))

;; Post specs
(s/def ::post-data (s/keys :req-un [::title ::date ::section ::excerpt ::file-name]
                           :opt-un [::meta ::perf-report]))
(s/def ::post (s/keys :req-un [::post-data ::content]))
(s/def ::posts (s/coll-of ::post))

(defrecord Post [post-data content])

(defn create-post
  "Creates a post record from post data and content"
  [post-data content]
  {:pre [(s/valid? ::post-data post-data)
         (s/valid? ::content content)]}
  (->Post post-data content))


;; Blog specs
(s/def ::short string?)
(s/def ::long string?)
(s/def ::terminal string?)
(s/def ::home-section string?)
(s/def ::blog-title (s/keys :req-un [::short ::long ::terminal]))
(s/def ::sections (s/coll-of ::section))
(s/def ::blog (s/keys :req-un [::blog-title ::sections ::home-section]))

(defrecord Blog [blog-title])

(defn create-blog
  "Creates a blog record from title and date"
 ([blog-data]
  {:pre [(s/valid? ::blog blog-data)]}
  (map->Blog blog-data)))
