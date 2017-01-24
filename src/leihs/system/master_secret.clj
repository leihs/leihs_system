(ns leihs.system.master-secret
  (require
    [leihs.system.state :as state]

    [clojure.java.io :refer [file]]
    [clojure.string :refer [trim]]
    [hawk.core :as hawk]

    [clojure.tools.logging :as logging]
    ))

(defn file-change-handler [event]
  (logging/info event (-> event :file .getName))
  (case (:kind event)
    (:initial
      :modify
      :create) (try (let [secret (-> event :file slurp trim)]
                      (swap! state/db assoc :master-secret secret))
                    (catch Exception e
                      (logging/warn "failed to read secret" e)))
    (:delete) (try (swap! state/db assoc :master-secret nil)
                   (catch Exception e
                     (logging/warn "failed to read secret" e)))))

(defn initialize []
  (file-change-handler {:kind :initial
                        :file (clojure.java.io/file "../config/master-secret.txt")})
  (hawk/watch!
    [{:paths ["../config"]
      :handler (fn [ctx event] (#'file-change-handler event) ctx)
      :filter (fn [_ event]  (= "master-secret.txt" (-> event :file .getName)))}]))
