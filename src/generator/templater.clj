(ns generator.templater
  (:require [clojure.string :as str]
            [generator.template_model :as model]
            [clojure.spec.alpha :as s]
            [generator.util :as util]))

(declare eval-template)

(def date-formatter (util/create-date-formatter))

(defn format-date
  [date]
  (.format date-formatter date))

(defn- find-code-block
  "Finds the first location of a code block {{ ... }} or multi-line {% ... %}."
  [template]
  (let [multi-line (re-find #"(?s)\{%(.*?)%\}" template)
        single-line (re-find #"\{\{([^}]+)\}\}" template)]
    (cond
      (not (nil? multi-line)) multi-line
      (not (nil? single-line)) single-line
      :else nil)))

(defn- eval-expression
  [expr blog section post]
  (let [code (read-string expr)
        eval-expr `(let [~'blog ~blog
                         ~'section ~section
                         ~'post ~post
                         ~'format-date (fn [date#] (format-date date#))
                         ~'apply-section (fn [path#] (slurp path#))
                         ~'apply-section-with-post (fn [path# post#] (eval-template (slurp path#) ~blog ~section post#))]
                     ~code)]
    (eval eval-expr)))

(defn eval-template
  "Evluates the template with the given post context"
  [template blog section post]
  {:pre [(s/valid? string? template)
         (s/valid? ::model/TemplateBlog blog)
         (s/valid? ::model/TemplateSection section)
         (or (nil? post) (s/valid? ::model/TemplatePost post))]}
  (loop [template template]
    (let [found-block (find-code-block template)]
      (if (nil? found-block)
        template
        (let [[raw expr] found-block
              execution (str (eval-expression expr blog section post))
              replacement (if (nil? execution) "" execution)]
          (recur (str/replace-first template raw replacement)))))))
