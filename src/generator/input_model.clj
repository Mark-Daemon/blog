(ns generator.input_model
  (:require [clojure.spec.alpha :as s]))

;; Basic specs for fields
(s/def ::title string?)
(s/def ::date inst?)
(s/def ::content string?)
(s/def ::section string?)
(s/def ::excerpt string?)
(s/def ::file-name string?)

;; Post specs
(s/def ::post (s/keys :req-un [::title ::date ::section ::excerpt ::file-name ::content]
                      :opt-un [::meta]))
(s/def ::posts (s/coll-of ::post))
(defrecord Post [title date section excerpt file-name content])
(defn create-post
  "Creates a post record from post data and content"
  [post]
  {:pre [(s/valid? ::post post)]}
  (map->Post post))


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
