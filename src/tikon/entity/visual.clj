(ns tikon.entity.visual
  (:require [clojure.string :as s]))

(defn kw [p] 
  (apply str (rest (replace {\- \ } (str p)))) )

(defn kw-or [p]
  (if-let [p (kw p)]
    p
    "nothing"))

(defn apperance [h]
  (let [a (:apperance h)]
    (str 
      (str (:general a))
      " "
      (kw (:height a))
      " "
      (kw (:sex h))
      ", with "
      (kw (-> a :complication :general))
      " complication, "
      (kw (-> a :complication :breasts))
      " breasts and "
      (kw (-> a :complication :butt))
      " butt, "
      (kw (:skin a))
      " skin and "
      (kw (:hair-style a))
      " "
      (kw (:hair-color a))
      " hair"
      )))

(defn or-no 
  ([c p] (if (not= (kw c) "") (str "" (kw c) ", ") p))
  ([c] (or-no c "")))

(defn cloth [h]
  (let [c (:cloth h) ]
    (apply 
      str 
      (drop-last 
        2
        (str
          (or-no (:top c))
          (or-no (:bottom c))
          (or-no (:feet c))
          (or-no (:head c))
          (if (= :female (:sex h))
            (str
              (if (= nil (:top c))
                (or-no (-> c :bra) " no bra, "))
              (if (= nil (:bottom c))
                (or-no (-> c :panties) " no panties, ")
                )))
          )))))

