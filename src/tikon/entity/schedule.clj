(ns tikon.entity.schedule
  (:require [tikon.world.time :as t]))

(defn schedule []
  [])

(def default-options 
  {:priority 0})

(defn conj-schedule
  "Conjoin ~forms to ~schedule. Forms must be collections of 3-element collections - time from, time to (by fn t) and action (by fn d)."
  [schedule & forms]
  (reduce (fn [schedule [tf action & [options]]]
            (conj schedule (list tf (assoc action :options (if options options default-options)))))
          schedule
          forms))

(defn travel-to [location-id]
  {:do :travel
   :to location-id})

(defn change-clothing [cloth]
  {:do :dress
   :cloth cloth})

(defn position [& pos]
  {:do :position
   :position (if pos pos [])})

(defn t 
  "Get time from ~minute, ~hour, ..."
  [& [minute hour day week month]]
  (list (if month month -1) (if week week -1) (if day day -1) (if hour hour -1) (if minute minute -1)))

(defn d 
  "Get task from ~args"
  [& args]
  (case (first args)
    :travel (apply travel-to (rest args))
    :position (apply position (rest args))
    :dress (apply change-clothing (rest args))))

(defn actual-task 
  "Get actual task for time ~t from ~schedule"
  [schedule t]
  (let [times (t/times t)]
    (map second 
         (filter 
           (fn [[-t _]] 
             (= (apply t/realtime (t/fill-times times -t)) t))
           schedule))))
