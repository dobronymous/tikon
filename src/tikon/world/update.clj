(ns tikon.world.update
  (:require [tikon.entity.update :as eu]
            [tikon.world.time :as t]))

(defn update [world]
  (loop [update (vals (:entities world)) world world]
    (if (empty? update)
      world
      (recur (rest update) (eu/update world (first update))))))

(defn pass-time [world mins]
  (loop [t 0 w world]
    (let [new-world (update (t/pass w 1))]
      (if (> mins (inc t))
        (recur (inc t) new-world)
        new-world))))
