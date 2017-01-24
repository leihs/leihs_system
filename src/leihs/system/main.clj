(ns leihs.system.main
  (:require
    [leihs.system.master-secret :as master-secret]
    [leihs.system.web :as web]
    [leihs.system.repl]

    [config.core :refer [env]]
    [ring.adapter.jetty :refer [run-jetty]]

    [clojure.tools.logging :as logging]
    )
  (:gen-class))


(defn -main [& args]
  (master-secret/initialize)
  (if (env :dev)
    (leihs.system.repl/start-server)
    (let [port (Integer/parseInt (or (env :port) "3220"))]
      (run-jetty web/app {:port port :join? false}))))
