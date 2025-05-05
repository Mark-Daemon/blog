(ns generator.generate-clojurescript
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [generator.util :as util]
            [cljs.build.api :as cljs]))

(defn- compile-cljs
  [file target-file]
  (try
    (let [relative-path (str/replace (.getPath file) #"^data/" "")
          ns-name (-> relative-path
                      (str/replace #"\.cljs$" "")
                      (str/replace #"/" "."))
          tmp-output-dir (str (.getParent target-file) "/out")
          opts {:main          ns-name
                :output-to     (.getPath target-file)
                :output-dir    tmp-output-dir
                :source-paths  ["data"]
                :optimizations :advanced
                :verbose       false
                :pretty-print  false}]
      (io/make-parents target-file)
      (cljs/build file opts)
      (util/delete-directory tmp-output-dir))
    (catch Exception e
      (println "Error compiling" file ":" (.getMessage e))
      (throw e))))

(defn- process-cljs-file
  [file]
  (let [file-path (.getPath file)
        relative-path (str/replace file-path #"^data/" "")
        target-path (str util/publish-folder (str/replace relative-path #"\.cljs$" ".js"))
        target-file (io/file target-path)]
    (io/make-parents target-file)
    (compile-cljs file target-file)))

(defn compile-and-copy-clojurescript!
  []
  (let [files (->> (filter #(.isFile %) (file-seq (io/file "data")))
                   (filter #(str/ends-with? (.getName %) ".cljs")))
        tasks (map (fn [file] #(process-cljs-file file)) files)]
    (util/resolve-futures (map util/to-future tasks))))
