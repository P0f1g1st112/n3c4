(ns united.web
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [clojure.java.jdbc :as db]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [ring.util.request :refer [body-string]]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :refer [wrap-cors]]
            ))

(def db-adr "postgres://bnesfybfmmkphq:74d50eec43a5a79466c9ebaeb624aa1fa11a64e2d24e14d56579cc9b400f180e@ec2-54-166-107-5.compute-1.amazonaws.com:5432/d1li66jgj4vf4k")

(defn records []
        (-> (db/query db-adr ["select * from patients"])
            (json/write-str)
            (response))
)
;;middleware for ser-deser
;;repl-driven dev
(defn take-rec [row]
  (let [rec (edn/read-string (body-string row))]
    (db/insert! db-adr :patients {:name (:name rec)
                                  :sex (:sex rec)
                                  :birth (:birth rec)
                                  :address (:address rec)
                                  :oms (:oms rec)})
    )
  {:status 200
   :body ""}
  )

(defn update-rec [row]
  (let [rec (edn/read-string (body-string row))]
    (db/update! db-adr :patients {:name (:name rec)
                                  :sex (:sex rec)
                                  :birth (:birth rec)
                                  :address (:address rec)
                                  :oms (:oms rec)}
                       ["id = ?" (:id rec)])
    (println "Updated: " rec))
  )

(defn delete-rec [row]
  (let [rec (edn/read-string (body-string row))]
    (println "Deleted with id " (:id rec))
    (db/delete! db-adr :patients ["id = ?" rec])
    {:status 200
     :body ""})
  )

(defroutes app
  (GET "/read" [] (records))
  (POST "/create" req (take-rec req))
  (POST "/update" req (update-rec req))
  (POST "/delete" req (delete-rec req))
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body (pr-str "OK 200")})
  (ANY "*" [] "<h1>404</h1>")
)

(defn cors [] (wrap-cors app :access-control-allow-origin [#"http://localhost:5000" #"http://.*" #"https://.*"]
                         :access-control-allow-methods [:get :put :post :delete]
                         :access-control-allow-credentials "true"))

(defn -main []
    (jetty/run-jetty (cors) {:port 5000 :join? false}))
