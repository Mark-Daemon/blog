(ns generator.generate_rss
  (:require [clojure.string :as str]
            [generator.input_model :as model]
            [clojure.spec.alpha :as s]
            [generator.util :as util]))

(defn- create-open-rss-tag
  "Create the rss-version, and channel data tag. Arguments then pass it item content"
  [blog-data items]
  (let [str-builder (StringBuilder.)]
    (.append str-builder "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
    (.append str-builder "<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n")
    (.append str-builder "<channel>\n")
    (.append str-builder "<title>")
    (.append str-builder (:title blog-data))
    (.append str-builder "</title>\n")
    (.append str-builder "<link>")
    (.append str-builder (:link blog-data))
    (.append str-builder "</link>\n")
    (.append str-builder "<description>")
    (.append str-builder "Mark Daemon: Tech, Statistics, Literature, and Politics")
    (.append str-builder "</description>\n")
    (.append str-builder (str "<atom:link href=\"" (:link blog-data) "/rss.xml\" "
                              "rel=\"self\" type=\"application/rss+xml\"/>\n"))

    (doseq [item items]
      (.append str-builder item))

    (.append str-builder "</channel>\n")
    (.append str-builder "</rss>\n")))

(defn- generate-rss-item
  "Creates an item entry for the post"
  [base-url post-data date-formatter]
  {:pre [(s/valid? ::model/post post-data)
         (s/valid? string? base-url)]}
  (let [str-builder (StringBuilder.)]
    (.append str-builder "<item>\n")
    (.append str-builder "<title>")
    (.append str-builder (:title post-data))
    (.append str-builder "</title>\n")
    (.append str-builder "<link>")
    (.append str-builder (str base-url "/" (:section post-data) "/" (str/replace (:file-name post-data) ".md" ".html")))
    (.append str-builder "</link>\n")
    (.append str-builder "<description>")
    (.append str-builder (:excerpt post-data))
    (.append str-builder "</description>\n")
    (.append str-builder "<pubDate>")
    (.append str-builder (.format date-formatter (:date post-data)))
    (.append str-builder "</pubDate>\n")
    (.append str-builder "<guid isPermaLink=\"true\">")
    (.append str-builder (str base-url "/" (:section post-data) "/" (str/replace (:file-name post-data) ".md" ".html")))
    (.append str-builder "</guid>\n")
    (.append str-builder "</item>\n")
    (.toString str-builder)))

(defn generate-rss-feed!
  "Generates the RSS feed for the site and stored it in the output directory"
  [blog-data posts-data]
  {:pre [(s/valid? ::model/posts posts-data)
         (s/valid? ::model/blog blog-data)]}
  (let [base-url (:link blog-data)
        date-formatter (util/create-RFC-822-date-formatter)
        items (map #(generate-rss-item base-url % date-formatter) posts-data)]
    (->> (create-open-rss-tag blog-data items)
         (spit (str util/publish-folder "rss.xml")))))
