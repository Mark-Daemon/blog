(ns generator.generate_performance
  (:require [clojure.string :as str]
            [generator.util :as util]))

(defn generate-performance-report
  "Generates the performance report and returns the HTML content"
  [mental physical productivity date]
  {:pre [(number? mental)
         (number? physical)
         (number? productivity)
         (inst? date)]}
    (-> (slurp "data/templates/performance-report.html")
        (str/replace "{{ performance.date }}" (str/upper-case (.format (util/create-date-formatter) date)))
        (str/replace "{{ performance.mental }}" (str mental "%"))
        (str/replace "{{ performance.physical }}" (str physical "%"))
        (str/replace "{{ performance.productivity }}" (str productivity "%"))))
