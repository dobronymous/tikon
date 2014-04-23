(ns tikon.game.interact
  (:require [tikon.game.core :refer :all]
            [tikon.entity.update :as eu]
            [tikon.entity.core :as e]
            [tikon.world.time :as t]
            [tikon.world.update :as wu]
            [tikon.world.core :as w]
            [tikon.entity.relationships :as rl]
            [tikon.ui.core :as ui]
            [tikon.entity.visual :as ev]
            [tikon.game.sex.foreplay]))

(def topics [:politics :vg :music :art :relationships :science])

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

     (str "---------------------------")))) 

;; chat

(defn chat-exit [entity gs]
  (p
   (header gs entity)
   (str "Sorry, I need to go to " (e/get-location (e/refresh gs entity)))
   back-link))

(defn chat-about [entity topic gs]
  (cond-> gs
    true 
    (wu
      (eu/chat "me" (:name entity) topic)
      (wu/pass-time 30))
    (not= (e/get-location (e/refresh gs entity)) (e/get-location (w/get-entity (:world gs) "me"))) 
    (-> pop-page
        pop-page
        (switch-page (partial chat-exit entity)))))

(defn chatting [entity gs]
  (p
   (header gs entity)
   (map #(link (str %) (partial chat-about entity %)) topics)
   back-link))

(defn chat [entity gs]
  (switch-page gs (partial chatting entity)))

;; undress

(defn undress-this [entity cloth gs]
  (wu gs
      (eu/undress entity cloth)))

(defn undress [entity gs]
  (let [entity (e/refresh gs entity)]
    (p 
      (header gs entity)
      (map (fn [[k v]]
             (link (str v) (partial undress-this entity k)))
           (filter (fn [[_ v]] v) (:cloth entity)))
      back-link)))

;; dress

(defn dress-this [entity item pos gs]
  (wu gs
      (eu/dress entity pos)))

(defn dress [entity gs]
  (let [entity (e/refresh gs entity)]
    (p
     (header gs entity)
     (map-indexed (fn [i [slot item]] 
                    (link (str item) (partial dress-this entity [slot item] i)))
                  (e/get-inventory entity))

     back-link)))

;; date

(def date-locations ["beach" "home"])

(defn really-undate [entity gs]
  (-> gs
      (wu 
        (eu/end-date entity))
      pop-page
      pop-page))

(defn undate [entity gs]
  (p
   (header gs entity)
   "Really?"
   (link "Yes" (partial really-undate entity))
   back-link))

(defn date-there [entity location gs]
  (-> gs
      (wu 
        (eu/change-relation entity (w/get-entity (:world gs) "me") 60)
        (eu/date entity location)
        (eu/travel "me" location))
      (assoc :camera location)
      (pop-page)
      (pop-page)))

(defn date [entity gs]
  (p
   (header gs entity)
   (str (get-in entity [:performing]))
   (if (>= (get-in (e/get-performing entity) [:options :priority]) 5)
     "Sorry, I'm too busy now"
     (map #(link % (partial date-there entity %)) date-locations))
   back-link))

;; information

(defn ask-about [entity about gs]
  (p
   (header gs entity)
   (case about
     :interests (str (:knowledge entity))
     :else "") 
   back-link))

(defn information [entity gs]
  (let [entity (e/refresh gs entity)]
    (p
     (header gs entity)
     (link "interests" (switch-link (partial ask-about entity :interests)))
     back-link)))

(defn main [entity-name gs]
  (let [world (:world gs)
        entity (get-in gs [:world :entities entity-name])
        myself (get-in gs [:world :entities "me"])]
    (p
     (header gs entity)
     (if (>= (e/get-performing-priority entity) 10)
       "Sorry, I'm too busy at the moment"
       (list
         (if (rl/can-chat? myself entity)
           (list (link "Chat" (partial chat entity))
                 (link "Ask ..." (switch-link (partial information entity))))
           "! no chat")
         (if (rl/can-foreplay? myself entity)
           (link "Foreplay" (switch-link (partial tikon.game.sex.foreplay/main entity)))
           "! no foreplay")
         (if (rl/can-dress? myself entity)
           (list
             (link "Undress" (switch-link (partial undress entity)))
             (link "Dress" #(switch-page % (partial dress entity))))
           "! no undress")
         (if (rl/can-date? myself entity)
           (if (= (get-in entity [:performing]) :date)
             (link "End date" (switch-link (partial undate entity)))
             (link "Date" (switch-link (partial date entity))))
           "! no date")))
     back-link)))
