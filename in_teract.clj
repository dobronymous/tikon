(ns tikon.game.in-teract
  (:require [lanterna.screen :as s]
            [tikon.entity.visual :as ev]
            [tikon.entity.update :as eu]
            [tikon.ui.core :as ui]))

(def topics [:politics :vg :music :relatioships :art :science])

(defn control [entity s gs]
  (let [world (:world gs) 
        location (get-in world [:locs (:camera gs)])
        input (s/get-key-blocking s)]
    (cond
      (= input \q) (update-in gs [:dispatchers] rest)
      (= input \1) (assoc-in gs [:interact-state] :chat)
      :else gs)))

(defn chat [entity s gs]
  (let [world (:world gs) 
        location (get-in world [:locs (:camera gs)])
        input (s/get-key-blocking s)
        as-int (try (Integer/parseInt (str input)) (catch Exception e nil))
        myself (get (:entities world) "me")

        chat-on (fn []
                  (assoc gs :world (eu/chat world myself entity (nth topics (dec as-int)))))
        ]
    (cond
      (= input \q) (assoc-in gs [:interact-state] nil)
      ((set (range 10)) as-int) (chat-on)
      :else gs)))

(defn dispatch! [entity s -gs]
  (s/clear s)
  (->> [0]
       (ui/paint-text 
         s
         [(str "Interacting with " (:name entity))
          "--------------"
          (ev/apperance entity)
          (ev/cloth entity)
          (str (:relationships entity))
          "--------------"
          "1. chat"
          ]
         60 0)
       ((fn [oy]
          (case (:interact-state -gs)
            :chat (ui/paint-text s (cons "Chat about: " (map-indexed #(str (inc %1) ". " %2 ) topics)) 60 0 oy)
            oy)
          )))

  (s/redraw s)
  (case (:interact-state -gs)
    :chat (chat entity s -gs)
    (control entity s -gs)))

