(ns posts.tools.week_scoring.scoring
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [goog.events :as gevents]))

;; Calculations

(defn- calculate-sleep-score
  "Calculates sleep score based on bedtime and wake-up time (in 24-hour format)"
  [bedtime wake-time]
  (if (or (nil? bedtime) (nil? wake-time))
    0
    (let [;; Bedtime score: Target 11pm (23:00), 0 at 3am (27:00)
         bedtime-score (max 0
                            (min 100
                                 (- 100 (* 25 (- bedtime 23)))))
         ;; Wake-up score: Target 8am (8:00), 0 at 12pm (12:00)
         wake-score (max 0
                         (min 100
                              (- 100 (* 25 (js/Math.abs (- wake-time 8))))))
         ;; Total sleep score is average of bedtime and wake scores
         sleep-score (/ (+ bedtime-score wake-score) 2)]
     sleep-score)))

(defn calculate-sleep-scores
  "Calculates sleep scores for a week based on bedtimes and wake times"
  [sleep-entries]
  (let [sleep-scores (map (fn [[bed sleep]] (calculate-sleep-score bed sleep)) sleep-entries)
        max-sleep-score 700]
    (* 100 (/ (reduce + sleep-scores) max-sleep-score))))

(defn calculate-physical-score
  "Calculates physical activity score based on active days and workouts"
  [active-days workouts]
  (let [max-active-days 4
        max-workouts 3
        ;; Physical score: 40% from active days, 60% from workouts
        active-component (* 0.4 (/ active-days max-active-days))
        workout-component (* 0.6 (/ workouts max-workouts))
        physical-score (+ active-component workout-component)]
    (* physical-score 100)))

(defn calculate-productivity-score
  "Calculates productivity score based on completed vs planned tasks"
  [tasks-completed tasks-planned]
  (if (pos? tasks-planned)
    (* 100 (/ tasks-completed tasks-planned))
    0))

;; UI Interaction

(defn parse-time
  ([time-str]
   (parse-time time-str false))
  ([time-str is-bedtime]
   (if (or (str/blank? time-str)
           (not (re-matches #"^\d{1,2}:\d{2}$" time-str))) ; Validate HH:MM format
     nil
     (let [[hours mins] (str/split time-str #":")
           hours-int (js/parseInt hours)
           mins-int (js/parseInt mins)]
       (let [mins-decimal (/ mins-int 60)
               base-hours (+ hours-int mins-decimal)
               adjusted-hours (if (and is-bedtime
                                       (>= base-hours 0)
                                       (<= base-hours 12))
                                (+ base-hours 24)
                                base-hours)]
           adjusted-hours)))))

(defn get-sleep-entries
  "Collects sleep entries from input fields"
  []
  (let [days (range 1 8)
        bedtimes (map #(gdom/getElement (str "bedtime-day-" %)) days)
        wake-times (map #(gdom/getElement (str "wake-time-day-" %)) days)]
    (map (fn [[bed wake]] [(parse-time (.-value bed) true) (parse-time (.-value wake))])
     (filter (fn [[bed wake]] (and bed (not (nil? (.-value bed))) wake (not (nil? (.-value wake)))))
             (map list bedtimes wake-times)))))

(defn parse-int
  [s]
  (let [result (js/parseInt s)]
    (if (js/isNaN result)
      0
      result)))

(defn update-scores
  "Calculates and updates all scores"
  []
  (let [;; Get sleep scores
        sleeps (get-sleep-entries)
        sleep-score (calculate-sleep-scores sleeps)
        sleep-percentage (str (.toFixed sleep-score 0) "%")
        ;; Get physical scores
        active-days (parse-int (.-value (gdom/getElement "active-days")))
        workouts (parse-int (.-value (gdom/getElement "workouts")))
        physical-score (calculate-physical-score active-days workouts)
        physical-percentage (str (.toFixed physical-score 0) "%")
        ;; Get productivity scores
        tasks-completed (parse-int (.-value (gdom/getElement "tasks-completed")))
        tasks-planned (parse-int (.-value (gdom/getElement "tasks-planned")))
        productivity-score (calculate-productivity-score tasks-completed tasks-planned)
        productivity-percentage (str (.toFixed productivity-score 0) "%")
        ;; Get output elements
        sleep-output (gdom/getElement "sleep-score")
        sleep-bar (gdom/getElement "sleep-bar")
        physical-output (gdom/getElement "physical-score")
        physical-bar (gdom/getElement "physical-bar")
        productivity-output (gdom/getElement "productivity-score")
        productivity-bar (gdom/getElement "productivity-bar")]
    ;; Update outputs
    (set! (.-innerText sleep-output) sleep-percentage)
    (set! (.-innerText physical-output) physical-percentage)
    (set! (.-innerText productivity-output) productivity-percentage)
    (.setProperty (.-style sleep-bar) "--target-value" sleep-percentage)
    (.setProperty (.-style physical-bar) "--target-value" physical-percentage)
    (.setProperty (.-style productivity-bar) "--target-value" productivity-percentage)))

(defn save-input-data
  "Saves all input data to localStorage"
  []
  (let [data {:active-days (.-value (gdom/getElement "active-days"))
              :workouts (.-value (gdom/getElement "workouts"))
              :tasks-completed (.-value (gdom/getElement "tasks-completed"))
              :tasks-planned (.-value (gdom/getElement "tasks-planned"))
              :bedtimes (mapv #(.-value (gdom/getElement (str "bedtime-day-" %))) (range 1 8))
              :wake-times (mapv #(.-value (gdom/getElement (str "wake-time-day-" %))) (range 1 8))}]
    (.setItem js/localStorage "week-scoring-data" (.stringify js/JSON (clj->js data)))))

(defn load-input-data
  "Loads input data from localStorage and populates fields"
  []
  (when-let [saved-data (.getItem js/localStorage "week-scoring-data")]
    (let [data (js->clj (.parse js/JSON saved-data) :keywordize-keys true)]
      (set! (.-value (gdom/getElement "active-days")) (:active-days data))
      (set! (.-value (gdom/getElement "workouts")) (:workouts data))
      (set! (.-value (gdom/getElement "tasks-completed")) (:tasks-completed data))
      (set! (.-value (gdom/getElement "tasks-planned")) (:tasks-planned data))
      (doseq [i (range 7)]
        (set! (.-value (gdom/getElement (str "bedtime-day-" (inc i)))) (get-in data [:bedtimes i]))
        (set! (.-value (gdom/getElement (str "wake-time-day-" (inc i)))) (get-in data [:wake-times i]))))))

(defn try-update-scores
  "Attempts to update scores, catching any errors"
  []
  (try
    (update-scores)
    (save-input-data)
    (catch :default e
      (js/console.error "Error updating scores:" e))))

(defn reset-inputs
  "Clears all input fields and localStorage"
  []
  ;; Clear regular input fields
  (doseq [id ["active-days" "workouts" "tasks-completed" "tasks-planned"]]
    (set! (.-value (gdom/getElement id)) ""))
  ;; Clear sleep input fields
  (doseq [day (range 1 8)]
    (set! (.-value (gdom/getElement (str "bedtime-day-" day))) "")
    (set! (.-value (gdom/getElement (str "wake-time-day-" day))) ""))
  ;; Clear localStorage
  (.removeItem js/localStorage "week-scoring-data")
  ;; Update scores to reflect cleared state
  (try-update-scores))

(defn init
  "Initializes the scoring UI when DOM is loaded"
  []
  (gevents/listen
    js/document
    "DOMContentLoaded"
    (fn []
      (load-input-data)
      ;; Add event listeners to all inputs
      (doseq [id ["active-days" "workouts" "tasks-completed" "tasks-planned"]]
        (gevents/listen (gdom/getElement id) "input" try-update-scores))
      (doseq [day (range 1 8)]
        (gevents/listen (gdom/getElement (str "bedtime-day-" day)) "input" try-update-scores)
        (gevents/listen (gdom/getElement (str "wake-time-day-" day)) "input" try-update-scores))
      (gevents/listen (gdom/getElement "reset-button") "click" reset-inputs)
      ;; Initial score calculation
      (try-update-scores))))

(init)
