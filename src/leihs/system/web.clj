(ns leihs.system.web
  (:require

    [leihs.system.middleware :refer [wrap-middleware]]
    [leihs.system.auth]


    [compojure.core :refer [GET defroutes]]
    [compojure.route :refer [not-found resources]]
    [hiccup.page :refer [include-js include-css html5]]
    [config.core :refer [env]]

    [clojure.tools.logging :as logging]
    [logbug.debug :as debug :refer [I> I>> identity-with-logging]]
    [logbug.ring :refer [wrap-handler-with-logging]]
    ))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     [:div.container-fluid
      [:div.navbar.navbar-default.navbar-inverse {:style  "background-color: #770000;"}
       [:div.container-fluid
        [:div.navbar-header
         [:a.navbar-brand "Leihs System"]]]]
      mount-target]
     (include-js "/js/app.js")]))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))

  (resources "/")
  (not-found "Not Found"))

(def app
  (I> wrap-handler-with-logging
      #'routes
      wrap-middleware
      leihs.system.auth/wrap))

;;; DEBUG ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(debug/debug-ns *ns*)
