(ns leihs.system.prod
  (:require [leihs.system.ui :as ui]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(ui/init!)
