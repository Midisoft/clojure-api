(ns api-clojure.server
    (:require   [io.pedestal.http.route :as route]
                [io.pedestal.http :as http]))

(defn -main [& args]
    (println "Server started"))

(defn hello [request] {
    :status 200 
    :body (str "Hello, " (get-in request [:query-params :name] "Welcome!"))})

(def routes (route/expand-routes
                #{
                    ["/hello" :get hello :route-name :main]
                }))
  
(def service-map {
    ::http/routes   routes
    ::http/port     9999
    ::http/type     :jetty
    ::http/join?    false})
  
(http/start (http/create-server service-map))