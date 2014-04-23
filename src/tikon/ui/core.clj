(ns tikon.ui.core
  (:require [lanterna.screen :as s]
            [tikon.world.time :as t]))

(defn form [strings ln]
  (if (seq strings)
    (let [string (first strings)]
      (if (and (pos? ln) (> (count string) ln))
        (concat [(subs string 0 ln)] (form (concat [(subs string ln)] (rest strings)) ln))
      (concat [string] (form (rest strings) ln))))))

(defn paint-text [s strings w ox -oy]
  (loop [ox ox oy (last -oy) strings (form strings w)]
    (if (seq strings)
      (do 
        (s/put-string s ox oy (first strings))
        (recur ox (inc oy) (rest strings)))
     (concat -oy [oy]))))

(defn times-header [gs]
  (let [times (t/times (-> gs :world :time))]
    (list 
      (format "Now: %d week, %s %d:%d | Location: %s" 
              (:week times) (t/weekday (:day times)) (:hour times) (:minute times) (:camera gs))
      (str "―――――――――――――――――――――――――――――――――――――"))))
