(ns tikon.game.travel
  (:require [tikon.world.update :as wu]
            [tikon.world.core :as w]
            [tikon.entity.update :as eu]
            [tikon.game.core :refer :all]
            [tikon.game.interact]
            [tikon.ui.core :as ui]
            [tikon.world.time :as t]))

(defn wait [gs]
  (wu gs
      (wu/pass-time 60)))

(defn travel [passage gs]
  (-> gs
      (wu
        (eu/travel "me" (:to passage))
        (wu/pass-time (:lenght passage)))
      (assoc :camera (:to passage))))

(defn interact [entity gs]
  (switch-page gs (partial tikon.game.interact/main (:name entity))))

(defn main [gs]
  (let [world (:world gs)
        times (t/times (:time world))
        location (get-in world [:locs (:camera gs)])

        goto (map #(link (format "%s (%d mins)" (:to %) (:lenght %)) (partial travel %)) 
                  (:passages location))
        entities (map #(link (clojure.string/capitalize (:name %)) (partial interact %)) 
                      (w/get-entities world (:name location)))]
    (p
     (ui/times-header gs)
     goto
     entities
     (link "Wait (1h)" wait :key \.))))

