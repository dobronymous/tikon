(ns tikon.entity.update
  (:require [tikon.world.time :as t]
            [tikon.world.core :as w]
            [tikon.game.core :refer :all]
            [tikon.entity.core :as e]
            [tikon.entity.relationships :as rl]
            [tikon.entity.schedule :as schedule]))

(defn change-relation [world e1 e2 amount]
  (update-in world [:entities (:name e1)] #(rl/change % e2 amount)))

(defn change-pleasure [world e1 amount]
  (update-in world [:entities (:name e1) :pleasure] (partial + amount)))

(defn mood [gs entity]
  [:world :entities (:name entity) :mood])

(defn pleasure [gs entity]
  [:world :entities (:name entity) :pleasure])

(defn sexual-act [world e t]
  (let [[amount treshhold] (case t
                             :hang [(e/get-sensivity e :hang-sensivity) (e/get-sexual e :hang-treshhold)]
                             :kiss [(e/get-sensivity e :kiss-sensivity) (e/get-sexual e :kiss-treshhold)]
                             :touch-breasts [(e/get-sensivity e :breasts-sensivity) (e/get-sexual e :touch-treshhold)]
                             :touch-ass [(e/get-sensivity e :ass-sensivity) (e/get-sexual e :touch-treshhold)]
                             :touch-clit [(e/get-sensivity e :clit-sensivity) (e/get-sexual e :touch-treshhold)]
                             :touch-anal [(e/get-sensivity e :anal-sensivity) (e/get-sexual e :touch-treshhold)]
                 )]
    (if (<= (e/get-pleasure e) treshhold)
      (change-pleasure world e amount)
      world)))

(defn travel
  "Travel entity named ~n into ~location and return updated ~world"
  [world n location]
  (assoc-in world [:entities n :location] location))

(defn chat
  "Chat ~e(ntity)1 to ~e(ntity)2 on ~topic in ~world"
  [world e1 e2 topic]
  (let [entity1 (w/get-entity world e1)
        entity2 (w/get-entity world e2)
        interest (e/get-knowledge entity2 topic)
        amount (* interest (e/get-trust entity2))]
    (change-relation world entity2 entity1 amount)))

(defn date
  [world entity location]
  (-> world
      (travel (:name entity) location)
      (assoc-in [:entities (:name entity) :performing] :date)))

(defn end-date
  [world entity]
  (-> world
      (travel (:name entity) (e/get-home entity))
      (assoc-in [:entities (:name entity) :performing] nil)))

(defn perform [world human task]
  (let [date? (= (get-in human [:performing]) :date)]
    (if (not (or (and date? (> (get-in task [:options :priority]) 5)) (not date?)))
      world
      (->  (case (:do task)
             :travel (let [location (if (= :home (:to task)) (e/get-home human) (:to task))]
                       (travel world (:name human) location))
             :dress (assoc-in world [:entities (:name human) :cloth] (:cloth task))
             world)
          (assoc-in [:entities  (:name human) :performing] task)))))

(defn update 
  "Update ~human and return updated ~world"
  [world human]
  (loop [tasks (schedule/actual-task (:schedule human) (:time world))
         world world]
    (if (empty? tasks)
      world
      (recur
        (rest tasks)
        (perform world human (first tasks))))))

(defn undress
  "Undress ~part of the ~entity's weared clothes and put it in inventory"
  [world entity part]
  (update-in world 
             [:entities (:name entity)] 
             (fn [entity]
               (let [item [part (get-in entity [:cloth part])]]
                 (-> entity
                     (assoc-in [:cloth part] nil)
                     (update-in [:cloth-inventory] conj item))))))

(defn dress
  "Dress ~entity a item at ~item-pos position and remove it from inventory"
  [world entity item-pos]
  (let [[slot item] (nth (:cloth-inventory entity) item-pos)
        inventory (:cloth-inventory entity)

        dressed-item [slot (get-in entity [:cloth slot])]
        new-inventory (drop-nth item-pos 
                                (if (second dressed-item)
                                  (conj inventory dressed-item)
                                  inventory))]
    (update-in world
               [:entities (:name entity)]
               (fn [entity]
                 (-> entity
                     (assoc-in [:cloth slot] item)
                     (assoc-in [:cloth-inventory] new-inventory))))))
