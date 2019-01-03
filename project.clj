(defproject n2o "0.1.0-SNAPSHOT"
  :description "Distributed Application Server"
  :url "https://github.com/awkure/n2o"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [; [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojure "1.10.0-RC4"]
                 [org.clojure/core.typed "0.6.0"]
                 [org.clojure/test.check "0.9.0" :scope "test"]
                ;  [org.clojure/clojurescript "1.10.439"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.clojure/core.async "0.4.490"]
                 [org.immutant/immutant "2.1.10"] ; TODO : get rid of 
                 [clj-sockets "0.1.0"]
                 [clj-http "3.9.1"]
                 [clj-time "0.3.2"]]
  ; :plugins [[lein-cljsbuild "1.1.7"]]
  :main ^:skip-aot n2o.core
  :target-path "target/%s"
  ; :clean-targets ^{:protect false} ["target" "resources/public/js"]
  ; :cljsbuild {:builds [{:id "dev" 
  ;                       :source-paths ["src/n2o"]
  ;                       :figwheel true 
  ;                       :compiler {:main n2o.core
  ;                                  :asset-path "js"
  ;                                  :output-to "resources/public/js/main.js"
  ;                                  :output-dir "resources/public/js"
  ;                                  :verbose true 
  ;                                  :source-map-timestamp true}}]}
  :profiles {:uberjar {:aot :all}})
