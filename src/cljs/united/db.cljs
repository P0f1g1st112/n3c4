(ns united.db
  (:require
   [reagent.core :as r]
   [ajax.core :refer [GET]]))

(def recs (r/atom []))
(GET "http://localhost:5000/read" {:handler
                                   #(doseq [i (js->clj (.parse js/JSON %) :keywordize-keys true)]
                                      (swap! recs conj i)
                                      (println i))
                                   })
