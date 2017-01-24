(ns leihs.system.auth
  (:require

    [clojure.data.codec.base64 :as base64]
    [leihs.system.state :as state]

    [logbug.catcher :as catcher]
    [logbug.debug :as debug]
    [logbug.ring :refer [wrap-handler-with-logging]]



    ))

;;; BASIC ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def unauthorized-401
  {:status 401
   :headers
   {"WWW-Authenticate"
    (str "Basic realm=\"Leihs System; "
         "The master-secret is required!\"")}})

(defn require-authentication [request handler]
  (if-not (-> @state/db :master-secret)
    {:status 401
     :body "The master-secret has not been set yet!"}
    (if-not (or (-> request :basic-auth-request :username)
                (-> request :basic-auth-request :password))
      unauthorized-401
      (if-not (or (= (-> @state/db :master-secret)
                     (-> request :basic-auth-request :username))
                  (= (-> @state/db :master-secret)
                     (-> request :basic-auth-request :password)))
        unauthorized-401
        (handler request)))))

(defn wrap-require-authentication [handler]
  (fn [request] (require-authentication request handler)))

;;; BASIC ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- decode-base64
  [^String string]
  (apply str (map char (base64/decode (.getBytes string)))))

(defn extract-and-add-basic-auth-properties
  "Extracts information from the \"authorization\" header and
  adds a :basic-auth-request key to the request with the value
  {:name name :password password}."
  [request]
  (if-let [auth-header (-> request :headers (get "authorization" nil))]
    (catcher/snatch
      {:return-expr request}
      (let [decoded-val (decode-base64 (last (re-find #"^Basic (.*)$" auth-header)))
            [name password] (clojure.string/split (str decoded-val) #":" 2)]
        (assoc request :basic-auth-request {:username name :password password})))
    request))

(defn wrap-extract
  "Extracts information from the \"authorization\" header and
  adds  :basic-auth-request {:name name :password password}
  to the request if extraction succeeded. Leaves the request as
  is otherwise."
  [handler]
  (fn [request]
    (if-let [request-with-auth
             (catcher/snatch
               {:level :debug}
               (extract-and-add-basic-auth-properties request))]
      (handler request-with-auth)
      (handler request))))


(defn wrap [handler]
  (-> handler
      wrap-require-authentication
      wrap-extract))

;;; DEBUG ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(debug/debug-ns *ns*)
