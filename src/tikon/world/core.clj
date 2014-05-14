(ns tikon.world.core)

(defn world 
  "Initiate empty world"  
  []
  {:time 0
   :entities {}
   :locs {}})

(defn location 
  "Initiate empty location named ~n"
  [n & [sit lay bend]]
  {:name n
   :passages []
   :sit (set sit)
   :lay (set lay)
   :bend (set bend)})

(defn passage
  "Make passage from ~loc1 to ~loc2 in ~lenght"
  [loc1 loc2 length]
  {:from loc1 :to loc2 :lenght length})

(defn get-location [world locname]
  (get-in world [:locs locname]))

(defn add-entity 
  "Add ~e(ntity) to ~world at ~l(ocation)."
  [world l e]
  (update-in world [:entities] assoc (:name e) e))

(defn get-entity [world n]
  (get-in world [:entities n]))

(defn get-entities
  "Get entities in location named ~location-name from ~world"
  [world location-name]
  (filter #(and (not= (:name %) "me") 
                (= (:location %) location-name)) 
          (vals (:entities world))))

(defn add-location 
  "Add location ~loc into ~world"
  [world loc]
  (update-in world [:locs] assoc (:name loc) loc))

(defn conjoin-passage 
  "Conjoin passage to world"
  [world loc1 loc2 length]
  (update-in world [:locs loc1 :passages] conj (passage loc1 loc2 length)))

(defn add-passage
  "Add passage from ~loc1 to ~loc2 in ~world in ~length. Add backward passage if ~backward."
  [world loc1 loc2 & {:keys [length backward] :or {length 1 backward true}}]
  (cond-> world
    true (conjoin-passage loc1 loc2 length)
    backward (conjoin-passage loc2 loc1 length)))
