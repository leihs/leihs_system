(ns leihs.system.server
  (:require [leihs.system.web :as web]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3220"))]
     (run-jetty web/app {:port port :join? false})))
