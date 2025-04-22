(ns generator.generate-search
  (:require [clojure.spec.alpha :as s]
            [generator.template_model :as model]
            [clojure.string :as str]
            [generator.util :as util]
            [cheshire.core :refer :all]))

(defrecord SearchIndex [title excerpt date link])

(def date-formatter (util/create-date-formatter))

(def stop-words
  (set ["", "the", "when", "if" "and" "or" "but" "is" "are" "was" "were"
        "a" "an" "the" "this" "that" "it" "its" "in" "on" "at"
        "for" "to" "of" "with" "by" "from"]))

(defn- tokenise-post
  "Tokenises the post content into words"
  [content]
  (set (->> content
            (re-seq #"\w+")
            (map str/lower-case)
            (map str/trim)
            (filter #(not (contains? stop-words %))))))

(defn- parse-search-entry
  "Maps the post data to a search entry"
  [post]
  {:pre [(s/valid? ::model/TemplatePost post)]}
  (let [title (:title post)
        excerpt (:excerpt post)
        date (.format date-formatter (:date post))
        link (:link post)]
    (map->SearchIndex {:title   title
                       :excerpt excerpt
                       :date    date
                       :link    link})))

(defn- create-post-index
  "Creates a search index for the post"
  [terms post]
  {:pre [(s/valid? ::model/TemplatePost post)
         (associative? terms)]}
  (let [index-entry (parse-search-entry post)
        id (+ (get terms :last_id -1) 1)
        tokens (tokenise-post (:content post))]
    (-> (reduce #(assoc %1 %2 (conj (get %1 %2 []) id)) terms tokens)
        (assoc :last_id id)
        (assoc-in [:_ids id] index-entry))))

(defn generate_search_index!
  "Generates the search page for the site and stores it in the output directory"
  [blog]
  {:pre [(s/valid? ::model/TemplateBlog blog)]}
  (let [posts (flatten (map :posts (:sections blog)))
        index (reduce create-post-index {} posts)]
    (spit (str util/publish-folder "search.json") (generate-string (dissoc index :last_id)))))
