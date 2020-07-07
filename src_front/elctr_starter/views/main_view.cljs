(ns elctr-starter.views.main-view
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [cljs.pprint :as pp]
            [elctr-starter.views.utils :as vu :refer [v-box h-box h-split icon-btn3]]
            [elctr-starter.utils.icons :as ic]))

(def navbar-class "navbar")

(defn view [txt]
  [h-split
   :class [:h-full :overflow-hidden :m-2]
   :initial-split "66.6"
   :panel-2 [:div.h-full.w-full.bg-yellow-200
             [:h1.text-2xl.font-extrabold.text-gray-700.text-center
              txt]]
   :panel-1 [v-box
             :class "space-y-2"
             :children [[:div.h-12.bg-blue-100]
                        [:div.h-16.bg-blue-200]
                        [:div.h-full.bg-blue-300]]]])



(def views
  {:view/first  [view "first"]
   :view/second [view "second"]
   })
    
(defn nav-btn [icon on-click opts]
  (let [btnclass [:block  :lg:inline-block :lg:mt-0
                  :hover:text-white  :text-green-900]]
    [icon-btn3
     icon
     on-click
     (merge {:class btnclass} opts)]))  

(defn menu-area []
  (let [        f (fn [msg] #(rf/dispatch msg))]
   
   [:nav.flex.items-center.flex-wrap.bg-green-500.pl-4.p-2.space-x-4.navbar
    [nav-btn [ic/svg ic/rocket]   (f [:ui/change-viewL  :view/first   ]) {:title "first"}]
    [nav-btn [ic/svg ic/export] (f  [:ui/change-viewL  :view/second   ]) { :title "second" }]
    ]))

(defn show-msg [msg]
  (when msg
    (let [cmp (fn [] [:pre (-> msg pp/pprint with-out-str)])]
      [:div
       {:on-click #(rf/dispatch [:ui/show-info  cmp])}
       [:span 
        [:svg.fill-current.h-3.mr-2.inline {  :viewBox "0 0 20 20"}
         ic/info-circle]]
       (when (:time msg) 
         [:span ( str (:time msg) " - ")])
       
       (:msg msg)])))   

(defn stat-item [desc cmp]
  [h-box
   :children [[:div {:class "ml-1 w-1/12"} desc] cmp]])

(defn status []
  (r/with-let [
               msg (rf/subscribe [:ui/lastmsg])]
    [:footer.bg-gray-200.text-xs
     (stat-item "Log" [show-msg @msg]) ]))   

(defn inner-content []
  (r/with-let [view (rf/subscribe [:ui/selected-view])]
     (get views @view )))

(defn main-panel []
  [v-box
   :class "h-screen"; debug-grid-16 debug-black"
   :children  [
               [menu-area]
               [inner-content]
               [status]]])
