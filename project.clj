(defproject leihs-system "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Dual: GPL / EPL"}

  :dependencies [

                 [org.clojure/clojure "1.8.0"]

                 [cljsjs/bootstrap "3.3.6-1"]
                 [cljsjs/jquery "2.2.4-0"]
                 [compojure "1.5.1"]
                 [hawk "0.2.11"]
                 [hiccup "1.0.5"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail javax.jms/jms com.sun.jdmk/jmxtools com.sun.jmx/jmxri]]
                 [logbug "4.2.2"]
                 [org.clojure/clojurescript "1.9.293" :scope "provided"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.21"]
                 [reagent "0.6.0"]
                 [reagent-utils "0.2.0"]
                 [ring "1.5.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7" :exclusions [org.clojure/tools.reader]]
                 [yogthos/config "0.8"]

                 ]

  ; jdk 9 needs ["--add-modules" "java.xml.bind"]
  :jvm-opts #=(eval (if (re-matches #"^9\..*" (System/getProperty "java.version"))
                      ["--add-modules" "java.xml.bind"]
                      []))

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.1"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler leihs.system.web/app
         :uberwar-name "leihs-system.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "leihs-system.jar"

  :main leihs.system.main

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild
  {:builds {:min
            {:source-paths ["env/prod"]
             :compiler
             {:output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/uberjar"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["env/dev"]
             :compiler
             {:main "leihs.system.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}
            :test
            {:source-paths ["test"]
             :compiler {:main leihs.system.doo-runner
                        :asset-path "/js/out"
                        :output-to "target/test.js"
                        :output-dir "target/cljstest/public/js/out"
                        :optimizations :whitespace
                        :pretty-print true}}


            }
   }


  :figwheel
  {:http-server-root "public"
   :server-port 3229
   :nrepl-port 3228
   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"
                      ]
   :css-dirs ["resources/public/css"]
   :ring-handler leihs.system.web/app}


  :sass {:src "stylesheets"
         :dst "resources/public/css"}

  :profiles {:dev {:repl-options {:init-ns leihs.system.main
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.5.0"]
                                  [prone "1.1.4"]
                                  [figwheel-sidecar "0.5.8"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                  [pjstadig/humane-test-output "0.8.1"]
                                  ]

                   :source-paths ["env/dev"]
                   :plugins [[lein-figwheel "0.5.8"]
                             [lein-doo "0.1.6"]

                             [lein-sassy "1.0.7"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
