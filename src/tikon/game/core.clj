(ns tikon.game.core
  (:require [tikon.entity.update :as eu]
            [tikon.world.update :as wu]
            [tikon.world.time :as t]
            [tikon.entity.visual :as ev]
            [clojure.string]
            [tikon.ui.core :as ui]
            [lanterna.screen :as s]))

;; drop-nth

(defn drop-nth [n coll]
  (concat (take n coll) (nthrest coll (inc n))))

;; predicates 

(defn element? [s]
  (map? s))

(defn link? [s]
  ((every-pred element? #(= (:type %) :link)) s))

;; links 

(def key-codes (concat (range (int \1) (int \9)) (range (int \a) (int \z))))

(defn link [caption ifn & {:keys [key] :or [nil]}]
  {:type :link
   :caption caption
   :key key
   :fn ifn})

(defn link-index [i]
  (char (nth key-codes i)))

(defn link-index-str [counter el]
  (if (:key el) (:key el) (link-index counter)))

;; page

(defmacro p [& body]
  `(flatten (list ~@body)))

(defn stringpage [page]
  (second
    (reduce (fn [[counter strings] el]
              (if (link? el)
                [(inc counter) (conj strings (str (link-index-str counter el) ". " (:caption el)))]
                [counter (conj strings el)]))
            [0 []]
            page)))

(defn pop-page [gs]
  (update-in gs [:pages] rest))

(defn switch-page [gs page-cb]
  (update-in gs [:pages] conj page-cb))

(defn switch-link [page-cb]
  #(switch-page % page-cb))

(def back-link (link "Back" pop-page :key \q))

;; game state

(defmacro wu [gs & args]
  `(let [world# (:world ~gs)]
     (assoc ~gs :world (-> world# ~@args))))

(defn w [gs]
  (:world gs))

;; dispatch

(defn dispatch [s gs page]
  (let [page (page gs)
        links (reduce (fn [a link] 
                       (if (:key link) 
                         (assoc a (:key link) link)
                         (assoc a (link-index (-> a count)) link)))
                     {}
                     (filter link? page))]
    (s/clear s)
    (ui/paint-text s (stringpage page) 60 0 [ 0])
    (s/redraw s)

    (if-let [link (get links (s/get-key-blocking s) false)]
      (apply (:fn link) gs [])
      gs)))
