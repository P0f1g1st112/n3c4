(ns united.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [united.views :as views]
   [united.events :as events]
   [united.db :as db]
   ))

(defn
  ^:dev/after-load
  mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/render [views/main-panel] root-el)))
;;react - reagent - reframe- html
;; func tets

(defn init []
  (println @db/recs)
  (re-frame/dispatch-sync [::events/initialize-db])
  #_(re-frame/dispatch-sync [::events/upload-db])
  (mount-root))
