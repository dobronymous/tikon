(ns tikon.core
  (:gen-class)
  (:require [tikon.world.core :as world]
            [tikon.world.update :as wu]
            [lanterna.screen :as s]
            [tikon.game.travel]
            [tikon.game.core :as g]
            [tikon.game.world :as gw]))

(defn game-loop [s -gs]
  (let [page (first (:pages -gs))]
    (recur s (g/dispatch s -gs page))))

(defn -main [& args]
  (let [s (s/get-screen :swing)]
    (try
      (s/start s)
      (game-loop
        s
        {:camera "home"
         :pages (list tikon.game.travel/main)
         :world (gw/mk-world)})
      (finally (s/stop s)))))
