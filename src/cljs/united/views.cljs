(ns united.views
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [united.subs :as subs]
   [united.events :as events]
   [united.db :as db]
   [ajax.core :refer [GET POST]]
   ))

(defn sender [data act]
  (case act
  "c" (POST "http://localhost:5000/create" {:body data
                                            :handler prn})
  "d" (POST "http://localhost:5000/delete" {:body data
                                            :handler prn})
  "u" (POST "http://localhost:5000/update" {:body data
                                            :handler prn})
  ))

#_(def records (r/atom []))
#_(GET "http://localhost:5000/read" {:handler
                                   #(doseq [i (js->clj (.parse js/JSON %) :keywordize-keys true)]
                                      (swap! records conj i))
                                   })

(def to-send (r/atom {:id 0
                      :name ""
                      :sex "M"
                      :birth ""
                      :address ""
                      :oms ""}))

(defn form []
  [:div {:class "footer"}
   [:form {:style {:text-align "center"}
           :on-submit #( (.stopPropagation %))}
    [:input {:type "text" :placeholder "Полное имя"
             :id "name" :value (:name @to-send)
             :on-change #( (swap! to-send assoc :name
                                 (-> % .-target .-value))
                          (println (:name @to-send)))
                      }]
    [:select {:id "sex" :name "sex"
              :on-change #((swap! to-send assoc :sex
                                  (-> % .-target .-value))
                           (print (:sex @to-send)))}
     [:option {:value ""}]
     [:option {:value "M"} "М"]
     [:option {:value "F"} "Ж"]]
    [:input {:type "date" :id "birth" :placeholder "Дата рождения"
             :on-change #((swap! to-send assoc :birth
                                 (-> % .-target .-value))
                          (print (:birth @to-send)))}]
    [:br]
    [:input {:type "text" :id "address" :placeholder "Адрес"
             :on-change #((swap! to-send assoc :address
                                 (-> % .-target .-value)))}]
    [:input {:type "number" :inputMode "numeric" :id "oms"
             :placeholder "Номер полиса"
             :pattern "[0-9]{4}"
             :on-change #((swap! to-send assoc :oms
                                 (-> % .-target .-value)))}]
    [:br][:button
      {:id "create"
       :on-click #(#_(.preventDefault e)
      (if (.. js/document (getElementById "create"))
        (sender @to-send "c")
        (sender @to-send "u")
        ))} "submit"]
    [:h1 {:id "kostyl"} "kostyl"]

    ]])
(def counter (r/atom 0))

(defn updater [k rec]
  (set! (.. js/document (getElementById k) -value) ((keyword k) rec))
  (swap! to-send assoc (keyword k) (.. js/document (getElementById k) -value)))

(defn td-s [rec counter]
  [:tr {:id (:id rec)}
    [:td
      [:img {:src "delete.png"
             :on-click #((if (js/confirm "Удалить запись?")
                          ((sender (:id rec) "d")
                          (set! (.. js/document (getElementById (:id rec)) -id) "deleted")
                          ))
                            )}]
      [:img {:src "update.png"
             :on-click #((.. js/document (querySelector "#name") focus)
                         (updater "name" rec)
                         (updater "sex" rec)
                         (updater "birth" rec)
                         (updater "address" rec)
                         (updater "oms" rec)
      (swap! to-send assoc :id (:id rec))
      (println @to-send)
      (set! (.. js/document (getElementById "create") -id) "update")
                         )}]
     [:p {:id "counter"} counter]]
    [:td (:name rec)]
    [:td (:sex rec)]
    [:td (:birth rec)]
    [:td (:address rec)]
    [:td (:oms rec)]])

(defn loader []
  (rf/dispatch [::events/initialize-db])
  (set! (.. js/document (getElementById "loader") -style -display) "none")
  (set! (.. js/document (getElementById "hidden") -id) "showed"))


(defn main-panel []
  (do
    #_(def _name (rf/subscribe [::subs/name]))
    #_(def code (rf/subscribe [::subs/code]))
    (def db-recs (rf/subscribe [::subs/db-recs]))
    (js/setTimeout loader 3000)
    [:div
     [:div {:id "loader"}]
    [:div {:id "hidden"}
     #_[:p {:on-click #(rf/dispatch [::events/change-code])} "" @code]
     #_[:p "" @_name]
     #_[:p {:on-click #(rf/dispatch [::events/check-for-recs])}(str (first @db-recs))]
     [:p {:on-click #(rf/dispatch [::events/initialize-db])} "clk me for re-initialize"]
     [:h1 {:style {:text-align "center"}} "med dataset"]
      [:div {:class "table"}
       [:table
        [:thead [:tr
         [:th {:class "opts"} ""]
         [:th "Full Name"]
         [:th "Gender"]
         [:th "Date of birth"]
         [:th "Address"]
         [:th "OMS"]]]
        [:tbody
        (for [rec @db-recs]
          [td-s rec (swap! counter inc)])
        ]]][:br]
     [form]
     ]
     ]))
