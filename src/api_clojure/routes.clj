(ns api-clojure.core
    (:require   [io.pedestal.http.route :as route]
                [io.pedestal.http :as http]
                [io.pedestal.test :as test]))

(defn hello [request] {
    :status 200 
    :body (str "Hello, " (get-in request [:query-params :name] "Welcome!"))})

(def routes (route/expand-routes
                #{
                    ["/api" :get hello :route-name :api]
                }))
  
(def service-map {
    ::http/routes   routes
    ::http/port     9999
    ::http/type     :jetty
    ::http/join?    false})
  
(def server (atom nil))

(defn start-server []
    (reset! server (http/start (http/create-server service-map))))

(defn -main [& args]
    (println "Server started")
    (start-server))

;; (defn request-test [verb url] 
;;     (test/response-for (::http/service-fn @server) verb url))

;; (defn request-test :get "/api")