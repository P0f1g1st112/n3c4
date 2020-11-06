(ns grump2.views
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

(def records (r/atom []))
(defn sender [data]
  (POST "https://arcane-meadow-67682.herokuapp.com/create" {:body data 
                                      #_{:name "Ab Bc E"
                                       :sex "F"
                                       :birth "01.02.1999"
                                       :address "Street 2"
                                       :oms (:oms)}

                                        :handler prn}))
(GET "https://arcane-meadow-67682.herokuapp.com/read" {:handler 
                            (fn [resp] 
                              (doseq [i (s/split resp #"<br>")]
                                (swap! records conj (edn/read-string i)))
                              (println @records)
                                     )})

;(def click-count (r/atom 0))

;(defn b []
;  [:div
;   "The atom " [:code "click-count"] " has value: "
;   @click-count ". "
;   [:input {:type "button" :value "Click me!"
;            :on-click #(swap! click-count inc)}]])

(def to-send (r/atom {:name ""
                      :sex "M"
                      :birth ""
                      :address ""
                      :oms ""}))

(defn adder [e]
  (swap! to-send assoc :input-value
         (-> e .-target .-value))
  (prn @to-send))

(defn g []
  [:div
   [:form {:style {:text-align "center"}
           :on-submit #(fn [e] (.stopPropagation e))}
    [:br]
    [:input {:type "text" :placeholder "Полное имя" :id "name" :value (:name @to-send)
             :on-change (fn [e] 
                          (swap! to-send assoc :name
                                 (-> e .-target .-value))
                          (println (:name @to-send)))
;             :on-key-press (fn [e]
;                  (println "key press" (.-charCode e)))
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
    [:input {:type "text" :id "address" :placeholder "Адрес"
             :on-change (fn [e]
                          (swap! to-send assoc :address
                                 (-> e .-target .-value)))}]
    [:input {:type "number" :id "oms" :placeholder "Номер полиса"
             :on-change (fn [e]
                          (swap! to-send assoc :oms
                                 (-> e .-target .-value)))}]
    [:br][:button
     {:on-click #(fn [e]
                          (.preventDefault e)
                          (sender @to-send))} "save"]
   
    ]])

(defn main-panel []
  (do
    [:div {:id "parent"}
     [:h1 {:style {:text-align "center"}} "Med dataset"]
      [:div {:class "table"}
       [:table {:class "table-bordered table-hover"}
        [:thead [:tr
         [:th "Name"]
         [:th "Gender"]
         [:th "Birth date"]
         [:th "Address"]
         [:th "OMS"]]]
        (for [rec @records]
          [:tbody [:tr
           [:td (:name rec)]
           [:td (:sex rec)]
           [:td (:birth rec)]
           [:td (:address rec)]
           [:td (:oms rec)]]])]][:br]
;     [b]
     [g]
     ]))
