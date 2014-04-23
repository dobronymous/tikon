(ns tikon.entity.relationships
  (:require [tikon.entity.core :as e]))

(def relationship-level 
  (list [0 "neutral"] [30 "familiar"] [100 "friend"] [150 "good"]))

(defn test [entity entity2 pred]
  (pred (* (e/get-mood entity) (get-in entity [:relationships (:name entity2)] 0))))

(defn get [entity entity2]
  (get-in entity [:relationships (:name entity2)] 0))

(defn change [h1 h2 amount]
  (update-in h1 [:relationships (:name h2)] #(+ (if (nil? %) 0 %) (* amount (-> h1 :nature :trust)))))

(defn can-chat? [entity entity2]
  (>= (get entity entity2) 0))

(defn can-date? [f t]
  (or (test t f #(> % 30))
      (and (test t f #(> % 10)) (> (- (e/get-general-apperance f) (e/get-general-apperance t)) -20))))

(defn can-dress? [f t]
  (or (test t f #(> % 150))
      (and (test t f #(> % 50)) (> (e/get-liberation t) 50))))

(defn can-foreplay? [f t]
  (or (test t f #(> % 100))
      (and (test t f #(> % 30)) (> (e/get-liberation t) 50))))

(defn can-sex? [f t]
  (or (test t f #(> % 150))
      (and (test t f #(> % 50)) (> (e/get-liberation t) 50)))) 
