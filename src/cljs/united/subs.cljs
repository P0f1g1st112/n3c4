(ns united.subs
  (:require
   [re-frame.core :as rf]
   [united.db :as db])
)

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
 ::code
 (fn [db]
   (:code db)))

(rf/reg-sub
 ::db-recs
 (fn [db]
   (:records db)))
