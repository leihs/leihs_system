(ns leihs-system.prod
  (:require [leihs-system.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
