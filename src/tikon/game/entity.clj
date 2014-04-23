(ns tikon.game.entity
  (:require [tikon.entity.core :as e]
            [tikon.entity.schedule :as s]))

(def bikini (e/cloth nil nil nil :shoes :bikini-top :bikini-panties 0 0 0))
(def director-work (e/cloth nil :white-blouse-with-large-deco :short-black-skirt :shoes :bra :panties 0 0 0))
(def sleeping (e/cloth nil nil nil nil nil :panties 0 0 0))

(def director (e/human "director" "no" "directors home" e/general-nature
                       (e/apperance 80 :tall (e/complication :medium :big :big) :white :long :blonde)
                       e/general-cloth
                       (s/conj-schedule (s/schedule)
                                        [(s/t 0 1) (s/d :dress sleeping)]
                                        [(s/t 0 1) (s/d :travel :home) {:priority 4}]

                                        [(s/t 0 6) (s/d :dress e/general-cloth)]
                                        [(s/t 0 6) (s/d :travel :home)]

                                        [(s/t 0 8 0) (s/d :dress director-work)]
                                        [(s/t 0 8 0) (s/d :travel "directors cab") {:priority 10}]
                                        [(s/t 0 13 0) (s/d :travel "directors cab") {:priority 5}]

                                        [(s/t 0 8 1) (s/d :dress director-work)]
                                        [(s/t 0 8 1) (s/d :travel "directors cab") {:priority 10}]
                                        
                                        [(s/t 0 13 1) (s/d :travel "directors cab") {:priority 5}] 

                                        [(s/t 0 8 2) (s/d :dress director-work)]
                                        [(s/t 0 8 2) (s/d :travel "directors cab") {:priority 10}]
                                        [(s/t 0 13 2) (s/d :travel "directors cab") {:priority 5}]

                                        [(s/t 0 8 3) (s/d :dress director-work)]
                                        [(s/t 0 8 3) (s/d :travel "directors cab") {:priority 10}]
                                        [(s/t 0 13 3) (s/d :travel "directors cab") {:priority 5}]

                                        [(s/t 0 8 4) (s/d :dress director-work)]
                                        [(s/t 0 8 4) (s/d :travel "directors cab") {:priority 10}]
                                        [(s/t 0 13 4) (s/d :travel "directors cab") {:priority 5}]

                                        [(s/t 0 19) (s/d :dress e/general-cloth)]
                                        [(s/t 0 19) (s/d :travel :home)]

                                        [(s/t 30 7 6) (s/d :dress bikini)]
                                        [(s/t 0 8 6) (s/d :travel "beach")])
                       ))

(def me (e/human "me" "no" "home" e/general-nature e/general-apperance e/general-cloth 
                 (s/conj-schedule (s/schedule))))
