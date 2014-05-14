(ns tikon.game.world
  (:require [tikon.world.core :as w]
            [tikon.game.entity :as ge]))

(defn mk-world []
  (-> (w/world)
      (w/add-location (w/location "home" [:chair] [:bed]))
      (w/add-location (w/location "directors home" [:chair :armchair] [:bed :table] [:table :wall]))
      (w/add-location (w/location "school"))
      (w/add-location (w/location "beach"))
      (w/add-location (w/location "directors cab" [:armchair] [:soft :table] [:table :wall]))
      (w/add-location (w/location "biology cab"))

      (w/add-passage "home" "school" :length 20)
      (w/add-passage "home" "directors home" :length 20)
      (w/add-passage "home" "beach" :length 60)
      (w/add-passage "school" "directors cab")
      (w/add-passage "school" "biology cab")

      (w/add-entity "directors cab" ge/director)
      (w/add-entity "home" ge/me)))
