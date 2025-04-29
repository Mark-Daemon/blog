(ns generator.templater
  (:require [clojure.string :as str]
            [generator.template_model :as model]
            [clojure.spec.alpha :as s]
            [generator.util :as util]))

(declare eval-template)

(def date-formatter (util/create-date-formatter))

(defn format-date
  "Formats the date with the default style - used by the eval"
  [date]
  (.format date-formatter date))

(defn apply-section
  "Applies the section template to the given path - used by eval"
  [path]
  (slurp path))

(defn- enable-latex
  []
  (str """
  <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/katex@0.16.22/dist/katex.min.css\" integrity=\"sha384-5TcZemv2l/9On385z///+d7MSYlvIEw9FuZTIdZ14vJLqWphw7e7ZPuOiCHJcFCP\" crossorigin=\"anonymous\">
  <script defer src=\"https://cdn.jsdelivr.net/npm/katex@0.16.22/dist/katex.min.js\" integrity=\"sha384-cMkvdD8LoxVzGF/RPUKAcvmm49FQ0oxwDF3BGKtDXcEc+T1b2N+teh/OJfpU0jr6\" crossorigin=\"anonymous\"></script>
  <script type=\"module\">
      import renderMathInElement from \"https://cdn.jsdelivr.net/npm/katex@0.16.22/dist/contrib/auto-render.mjs\";
          window.addEventListener('DOMContentLoaded', () => {
                  const elements = document.getElementsByClassName(\"latex\");
                          for (let element of elements) {
                                katex.render(element.textContent, element, {
                                    throwOnError: true,
                                });
                          }
          });
  </script>
  """))

(defn- latex
  [& strings]
  (reduce #(str %1 "\n<span class=\"latex\">" %2 "</span>\n") ""  strings))

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
  (try (let [code (read-string expr)
             eval-expr `(let [~'blog ~blog
                              ~'section ~section
                              ~'post ~post
                              ~'apply-section-with-post (fn [path# post#]
                                                          (eval-template (slurp path#) ~blog ~section post#))]
                          ~code)]
         (binding [*ns* (find-ns 'generator.templater)]
           (eval eval-expr)))
       (catch Exception e
         (println "Error evaluating expression" expr)
         (println (.getMessage e))
         (throw e))))

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
