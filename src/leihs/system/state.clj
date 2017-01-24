(ns leihs.system.state
  (:require
    [clojure.tools.logging :as logging]
    ))


(defonce db (atom {:master-secret nil}))
