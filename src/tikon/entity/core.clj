(ns tikon.entity.core
  (:require [tikon.world.core :as w]))

(defn entity [n]
  {:name n
   :schedule []})

(defn human [n sn home nature apperance cloth schedule]
  {:name n
   :subname sn
   :home home
   :location home
   :relationships {"me" 100}


   :knowledge {:politics 10
               :vg 10
               :music 10
               :art 10
               :relationships 10
               :science 10}

   :sexual {:liberation 50

            :kiss-sensivity 8
            :hang-sensivity 5

            :breasts-sensivity 10
            :ass-sensivity 10
            :clit-sensivity 20
            :anal-sensivity 15

            :vagina-penetration 10
            :anal-penetration 10

            :kiss-treshhold 20
            :hang-treshhold 10
            :touch-treshhold 60
            :penetration-treshhold 100}
   :mood 1
   :pleasure 0
   :sex :female
   :age 20
   :position [:sitting :chair]

   :nature nature
   :apperance apperance
   :cloth cloth
   :cloth-inventory (list [:head :hat])
   :schedule schedule})

(defn apperance [general height complication skin hair-style hair-color]
  {:skin skin
   :hair-color hair-color
   :height height
   :complication complication
   :hair-style hair-style
   :general general})

(defn cloth [head top bottom feet bra panties _ _ _]
  {:top top
   :bottom bottom
   :feet feet
   :bra bra
   :panties panties
   :head head})

(defn complication [general breasts butt]
  {:general general
   :breasts breasts
   :butt butt})

(def general-apperance (apperance 50 :tall (complication :medium :medium :medium)  :white :medium :black))
(def general-cloth (cloth nil :jacket :skirt :schoes :bra :panties 0 0 0))
(def general-nature {:trust 0.3})

(def moods [[0.1 "very bad"] [0.5 "bad"] [1 "neutral"] [1.5 "good"] [2 "very good"] [2.5 "excellent"]])
(def pleasures [[0 "neutral"] [10 "bit excited"] [20 "somewhat excited"] [30 "excited"] [50 "very excited"] [70 "a lot excited"] [90 "orgasm"]])

(defn refresh [gs entity]
  (w/get-entity (:world gs) (:name entity)))

(defn get-general-apperance [entity]
  (get-in entity [:apperance :general]))

(defn get-liberation [entity]
  (get-in entity [:sexual :liberation]))

(defn get-inventory [entity]
  (:cloth-inventory entity))

(defn get-location [entity]
  (:location entity))

(defn get-home [entity]
  (:home entity))

(defn get-knowledge [entity at]
  (get-in entity [:knowledge at]))

(defn get-trust [entity]
  (get-in entity [:nature :trust]))

(defn get-performing [entity]
  (get-in entity [:performing]))

(defn get-performing-priority [entity]
  (get-in entity [:performing :options :priority] 0))

(defn get-mood [entity]
  (:mood entity))

(defn get-pleasure [entity]
  (:pleasure entity))

(defn get-cloth [entity slot]
  (get-in entity [:cloth slot]))

(defn get-sexual [entity k]
  (get-in entity [:sexual k]))

(defn get-sensivity [entity k]
  (let [basic (get-sexual entity k)]
    (case k
      :breasts-sensivity (cond-> basic 
                           (not (get-cloth entity :top)) (* 1.1) 
                           (not (get-cloth entity :bra)) (* 1.2))
      :ass-sensivity (cond-> basic 
                           (not (get-cloth entity :bottom)) (* 1.1) 
                           (not (get-cloth entity :panties)) (* 1.3))
      :clit-sensivity (cond-> basic 
                           (not (get-cloth entity :bottom)) (* 1.1) 
                           (not (get-cloth entity :panties)) (* 1.3))
      :anal-sensivity (cond-> basic 
                           (not (get-cloth entity :bottom)) (* 1.1) 
                           (not (get-cloth entity :panties)) (* 1.3))
      basic)))

(defn get-position [entity]
  (:position entity))

(defn set-position [entity pos]
  (assoc entity :position pos))
