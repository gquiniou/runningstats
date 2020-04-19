(ns runningstats.core
  (:gen-class)
  (:require [clojure-csv.core :as csv])
  (:require [clojure.java.io :as io])
  (:require [java-time :as jt]))


(defn loadcsv
  "parses CSV file and removes first and last row"
  []
  (rest (drop-last (csv/parse-csv (slurp (io/resource "runningdata.csv"))))))


(defn cleanrow
  "transforms a row of csv data into a map and removes unneeded columns"
  [row]
  (let [[id _ _ distance _ duration _ _ _ _ _ date] row
        parsed (jt/local-date "d/MM/yy H:mm" date)]
    {:runid id
     :distance (read-string distance)
     :duration duration
     :date (jt/format "E dd/MM/yyyy" parsed)
     :dayoffset (jt/time-between parsed (jt/local-date) :days)}))


(defn trailingdist
  "calculate trailing distance for previous x days"
  [x firstrun prevruns]
  (loop [[run & runs] prevruns
         total (:distance firstrun)]
    (if (or (nil? run)
            (<= x (- (:dayoffset run) (:dayoffset firstrun))))
      total
      (recur runs (+ total (:distance run))))))


(defn summarize
  "print top 3 results for kw"
  [data kw]
  (println "\n" kw " results")
  (doall (map #(printf "%-5.2f\t%s \n" (kw %) (:date %))
              (take 3 (sort #(compare (kw %2) (kw %1)) data)))))


(defn statistics
  "calculates and displays max distance for periods of 7 days, 30 days ..."
  [runs]
  (def results [])
  (loop [[run & prevruns] runs]
    (def results (conj results (assoc run :2days (trailingdist 2 run prevruns)
                                      :3days (trailingdist 3 run prevruns)
                                      :7days (trailingdist 7 run prevruns)
                                      :30days (trailingdist 30 run prevruns)
                                      :90days (trailingdist 90 run prevruns)
                                      :365days (trailingdist 365 run prevruns))))
    (when prevruns
      (recur prevruns)))
  (summarize results :2days)
  (summarize results :3days)
  (summarize results :7days)
  (summarize results :30days)
  (summarize results :90days)
  (summarize results :365days))


(defn -main
  "I don't do a whole lot ... yet."
  []
  (let [rows (map cleanrow (take 100000 (loadcsv)))]
    (statistics rows)))
