(ns united.views
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [clojure.string :as s]
   [clojure.edn :as edn]
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

(def records (r/atom []))
(GET "http://localhost:5000/read" {:handler 
                            (fn [resp] 
                              (doseq [i (s/split resp #"<br>")]
                                (swap! records conj (edn/read-string i)))
                              #_(println @records)
                                     )})

(def to-send (r/atom {:id 0
                      :name ""
                      :sex "M"
                      :birth ""
                      :address ""
                      :oms ""}))

(defn form []
  [:div {:class "footer"}
   [:form {:style {:text-align "center"}
           :on-submit (fn [e] (.stopPropagation e))}
    [:input {:type "text" :placeholder "Полное имя" 
             :id "name" :value (:name @to-send)
             :on-change (fn [e] 
                          (swap! to-send assoc :name
                                 (-> e .-target .-value))
                          (println (:name @to-send)))
                      }]
    [:select {:id "sex" :name "sex"
              :on-change (fn [e]
                           (swap! to-send assoc :sex
                                  (-> e .-target .-value))
                           (print (:sex @to-send)))}
     [:option {:value ""}]
     [:option {:value "M"} "М"]
     [:option {:value "F"} "Ж"]]
    [:input {:type "date" :id "birth" :placeholder "Дата рождения"
             :on-change (fn [e]
                          (swap! to-send assoc :birth
                                 (-> e .-target .-value))
                          (print (:birth @to-send)))}]
    [:br]
    [:input {:type "text" :id "address" :placeholder "Адрес"
             :on-change (fn [e]
                          (swap! to-send assoc :address
                                 (-> e .-target .-value)))}]
    [:input {:type "number" :inputMode "numeric" :id "oms" 
             :placeholder "Номер полиса"
             :pattern "[0-9]{4}"
             :on-change (fn [e]
                          (swap! to-send assoc :oms
                                 (-> e .-target .-value)))}]
    [:br][:button
      {:id "create"
       :on-click (fn [e]
                          #_(.preventDefault e)
      (if (.. js/document (getElementById "create"))
        (sender @to-send "c")
        (sender @to-send "u")
        ))} "submit"]
    [:h1 {:id "kostyl"} "kostyl"]
   
    ]])
(def counter (r/atom 0))
(defn td-s [rec counter]
  [:tr {:id (:id rec)}
    [:td
      [:img {:src "delete.png"
             :on-click (fn [e]
                        (if (js/confirm "Удалить запись?")
                          ((sender (:id rec) "d")
                          (set! (.. js/document (getElementById (:id rec)) -id) "deleted")
                          ))
                            )}]
      [:img {:src "update.png"
             :on-click (fn [e]
      (.. js/document (querySelector "#name") focus)
      (set! (.. js/document (getElementById "name") -value) (:name rec))
      (swap! to-send assoc :name
        (.. js/document (getElementById "name") -value))
      (set! (.. js/document (getElementById "sex") -value) (:sex rec))
      (swap! to-send assoc :sex
        (.. js/document (getElementById "sex") -value))
      (set! (.. js/document (getElementById "birth") -value)(:birth rec))
      (swap! to-send assoc :birth
        (.. js/document (getElementById "birth") -value))
      (set! (.. js/document (getElementById "address") -value)(:address rec))
      (swap! to-send assoc :address
        (.. js/document (getElementById "address") -value))
      (set! (.. js/document (getElementById "oms") -value) (:oms rec))
      (swap! to-send assoc :oms
        (.. js/document (getElementById "oms") -value))
      (swap! to-send assoc :id (:id rec))
      (println @to-send)
      (set! (.. js/document (getElementById "create") -id) "update")
                        )}][:p {:id "counter"} counter]]
    [:td (:name rec)]
    [:td (:sex rec)]
    [:td (:birth rec)]
    [:td (:address rec)]
    [:td (:oms rec)]])

(defn main-panel []
  (do
    [:div
     [:h1 {:style {:text-align "center"}} "Med dataset"]
      [:div {:class "table"}
       [:table
        [:thead [:tr
         [:th {:class "opts"} ""]
         [:th "Name"]
         [:th "Gender"]
         [:th "Birth date"]
         [:th "Address"]
         [:th "OMS"]]]
        [:tbody
        (for [rec @records]
          [td-s rec (swap! counter inc)])
        ]]][:br]
     [form]
     ]))
