(ns united.events
  (:require
   [re-frame.core :as rf]
   [united.db :as db]
   [ajax.core :refer [GET]]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   {:name "Med dataset"
    :code "200 OK"
    :url_host ""
    :records @db/recs}))

(rf/reg-event-db
 ::check-for-recs
 (fn [db _]
   (assoc db :records @db/recs)
   (println @db/recs)
   (println (:records db))
   (println db)))

(rf/reg-event-db
 ::change-code
 (fn [db _] ;;if data exists destruct is [db [_ data]]
   (assoc db :code "201 YET OK")))
