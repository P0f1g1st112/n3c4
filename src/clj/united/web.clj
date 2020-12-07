(ns united.web
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [clojure.java.jdbc :as db]
            [clojure.edn :as edn]
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


(defn do-rec [rec]
  (println rec)
  (db/insert! db-adr :patients {:name (:name rec)
                                :sex (:sex rec)
                                :birth (:birth rec)
                                :address (:address rec)
                                :oms (:oms rec)})
  (response "")
  )

(defn update-rec [rec]
  (println rec)
  (db/update! db-adr :patients {:name (:name rec)
                                :sex (:sex rec)
                                :birth (:birth rec)
                                :address (:address rec)
                                :oms (:oms rec)}
                     ["id = ?" (:id rec)])
    (response "")
  )

(defn delete-rec [rec]
  (println rec)
  (db/delete! db-adr :patients ["id = ?" rec])
  (response "")
  )

(defn reader [row act]
  (let [rec (edn/read-string (body-string row))]
    (case act
      "c" (do-rec rec)
      "u" (update-rec rec)
      "d" (delete-rec rec)))
)

(defroutes app
  (GET "/read" [] (records))
  (POST "/create" req (reader req "c"))
  (POST "/update" req (reader req "u"))
  (POST "/delete" req (reader req "d"))
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body (pr-str "OK 200")})
  (ANY "*" [] "<h1>404</h1>")
)

(defn cors [] (wrap-cors app :access-control-allow-origin [#"http://localhost:5000" #"http://.*" #"http://.*"]
                         :access-control-allow-methods [:get :put :post :delete]
                         :access-control-allow-credentials "true"))

(defn -main []
    (jetty/run-jetty (cors) {:port 5000 :join? false}))
