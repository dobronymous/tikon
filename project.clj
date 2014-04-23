(defproject tikon "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clojure-lanterna "0.9.4"]]
  :main ^:skip-aot tikon.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  
  :repl-options {:timeout 120000
                 :init (do ;; my dev stuff, dont ask about this
                           ;; anyway, I should use Stuart Sierra's reload
                           (in-ns 'user)
                           (load "autoload") 
                           (autoload/autoload-thread! "src/tikon") 
                           (println "Threaded autoloader engaged!")
                           (in-ns 'tikon.core)
                           (use 'clojure.repl)
                           (use 'clojure.pprint))}
  )
