(ns api-clojure.core
    (:require   [io.pedestal.http.route :as route]
                [io.pedestal.http :as http]))

;; "Data base"
(def db-users (atom {}))
;; {
;;     id {
;;         username
;;         email
;;     }
;; }

;; Route Functions
(defn hello [request] {
    :status 200 
    :body (str "Hello, " (get-in request [:query-params :name] "Welcome!"))})

(defn user-create [request] 
    (let [
        id       (java.util.UUID/randomUUID)
        username (get-in request [:query-params :username])
        email    (get-in request [:query-params :email])
    ]
    (swap! db-users assoc id { :username username :email email}) {
        :status 200
        :body "Usuário cadastrado com sucesso"
    }))

(defn get-users [request] {
    :status 200
    :body @db-users
})

(defn user-delete [request] 
    (let [
            id (get-in request [:path-params :id])
            delete-id (java.util.UUID/fromString id)
    ](swap! db-users dissoc delete-id) {
        :status 200
        :body "Usuário deletado com sucesso"
    }))

(defn user-update [request]
    (let [
        id (get-in request [:path-params :id])
        update-id (java.util.UUID/fromString id)
        username (get-in request [:query-params :username])
        email    (get-in request [:query-params :email])
    ]
    (swap! db-users assoc update-id { :username username :email email}) {
        :status 200
        :body "Usuário atualizado com sucesso"
    }))

;; Routes
(def routes (route/expand-routes
                #{
                    ["/api"                     :get hello :route-name :api]
                    ["/api/users"               :get get-users :route-name :get-users]
                    ["/api/users/create"        :post user-create :route-name :user-create]
                    ["/api/users/delete/:id"    :delete user-delete :route-name :user-delete]
                    ["/api/users/update/:id"    :put user-update :route-name :user-update]
                }))
  
;; Service map
(def service-map {
    ::http/routes   routes
    ::http/port     9999
    ::http/type     :jetty
    ::http/join?    false})
  

;; Server
(defonce server (atom nil))

(defn start-server []
    (reset! server (http/start (http/create-server service-map))))


;; Server Started
(defn -main [& args]
    (println "Server started")
    (start-server))