(ns tikon.game.sex.foreplay
  (:require [tikon.game.core :refer :all]
            [tikon.entity.update :as eu]
            [tikon.entity.core :as e]
            [tikon.world.time :as t]
            [tikon.world.update :as wu]
            [tikon.world.core :as w]
            [tikon.entity.relationships :as rl]
            [tikon.ui.core :as ui]
            [tikon.entity.visual :as ev]))

(defn header [gs entity]
  (let [entity (e/refresh gs entity)
        myself (w/get-entity (:world gs) "me")]
    (p
     (ui/times-header gs)
     (format "Interacting with %s" (:name entity))
     (str "-----------------------------------")
     (str "Apperance: " (ev/apperance entity))
     (str "Wearing " (ev/cloth entity))
     (let [relationship (rl/get entity myself)]
       (->> (take-while #(>= relationship (first %)) rl/relationship-level)
           last
           last
           (format "%s to you")))
     (let [mood (e/get-mood entity)]
       (->> (take-while #(>= mood (first %)) e/moods)
           last
           last
           (format "In %s mood")))
     (str (e/get-pleasure entity))
     (let [pleasure (e/get-pleasure entity)]
       (->> (take-while #(>= pleasure (first %)) e/pleasures)
           last
           last
           (format "%s")))

     (str "---------------------------")))) 

(defn act [entity t gs]
  (p
   (header gs entity)
   (let [[a k]
         (case t
           :hang ["hang" \1]
           :kiss ["kiss" \2]
           :touch-breasts ["touch breasts" \3]
           :touch-ass ["touch ass" \4])
         ]
     (list
       (str "You "
            a
            " " 
            (:name entity))
       (link "back" #(-> % (wu (eu/sexual-act entity t)) (pop-page)) :key k)))))

(defn main [entity gs]
  (let [entity (e/refresh gs entity)]
    (p
     (header gs entity)
     (link "hang" (switch-link (partial act entity :hang)))
     (link "kiss" (switch-link (partial act entity :kiss)))
     (link "touch breasts" (switch-link (partial act entity :touch-breasts)))
     (link "touch ass" (switch-link (partial act entity :touch-ass)))

     back-link)))
