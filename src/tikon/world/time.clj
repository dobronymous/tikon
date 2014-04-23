(ns tikon.world.time)

(def in-minute 1)
(def in-hour 60)
(def in-day (* 24 in-hour))
(def in-week (* in-day 7))
(def in-month (* in-week 4))

(defn weekday [d]
  (nth ["monday" "tuesday" "wensday" "thursday" "friday" "saturday" "sunday"] d))

(defn pass [world mins]
  (update-in world [:time] (partial + (* mins in-minute))))

(defn realtime
  "Return realtime based on ~times"
  ([month week day hour minute]
   (+ (* in-month month)
      (* in-week week)
      (* in-day day)
      (* in-hour hour)
      minute))
  ([times]
   (realtime (:month times) (:week times) (:day times) (:hour times) (:minute times))))
      
(defn fill-times [current times]
  (map #(if (= %1 -1) (%2 current) %1) times [:month :week :day :hour :minute])) 

(defn times 
  "Return times map based on realtime ~t"
  [t]
  (let [month (int (/ t in-month))
        t (- t (* month in-month))
        week (int (/ t in-week))
        t (- t (* week in-week))
        day (int (/ t in-day))
        t (- t (* day in-day))
        hour (int (/ t in-hour))
        minute (- t (* hour in-hour))]
    {:month month
     :week week
     :day day
     :hour hour
     :minute minute}))

(defn compare-times
  "Compare realtimes ~t1 with ~t2. Works like compare std fn"
  [t1 t2]
  (let [t1 (if (map? t1) (realtime t1) (apply realtime t1))
        t2 (if (map? t2) (realtime t2) (apply realtime t2))]
    (compare t1 t2)))

