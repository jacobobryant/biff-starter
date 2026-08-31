(ns com.example.app.demo
  (:require [com.biffweb.datastar :as biff.datastar :refer [signal-name]]
            [com.biffweb.fx :refer [defpipeline]]
            [com.biffweb.ring :refer [defpath]]
            [com.example.lib.middleware :as mid]
            [com.example.lib.ui :as ui]
            [com.example.routes :as routes]))

(defpath increment-clicks-path "/app/increment-clicks")
(defpath set-background-color-path "/app/background-color")

(def background-colors [:white :red :blue :green])

(defpipeline increment-clicks
  (fn [{:keys [session]}]
    {:_ [:biff.sqlite.fx/authorized-write
         {:update :user
          :set    {:user/n-clicks [:+ [:coalesce :user/n-clicks 0] 1]}
          :where  [:= :user/id (:uid session)]}]
     :status 204}))

(defpipeline set-background-color
  (fn [{:biff.datastar/keys [signals tab-id]}]
    (let [background-color (some-> (:tab/background-color signals) keyword)]
      {:_ [:biff.sqlite.fx/authorized-write
           {:insert-into   :tab-state
            :values        [{:tab-state/id   tab-id
                             :tab-state/data [:lift
                                              {:tab/background-color
                                               background-color}]}]
            :on-conflict   [:tab-state/id]
            :do-update-set [:tab-state/data]}]
       :status 204})))

(defn- background-class [background-color]
  (case background-color
    :red 'bg-red-100
    :blue 'bg-blue-100
    :green 'bg-green-100
    'bg-white))

(defpipeline demo-page
  [:biff.graph.fx/query
   [{:session/user [[:? :user/n-clicks]]}
    {:request/tab [:tab/background-color]}]]

  (fn [request {:keys [session/user request/tab]}]
    (let [n-clicks         (get user :user/n-clicks 0)
          background-color (:tab/background-color tab)]
      (ui/app-page
       request
       [:main {:id           "demo-page"
               :class        (conj '[flex flex-1 flex-col items-center
                                     justify-center gap-4 p-8]
                                   (background-class background-color))
               :data-signals (biff.datastar/signals-json
                              {:tab/background-color background-color})}
        [:button {:type          "button"
                  :class         '[rounded bg-blue-600 px-4 py-2 text-white]
                  :data-action   (increment-clicks-path)
                  :data-on:click "@post(el.dataset.action)"}
         (str "This button has been clicked " n-clicks " times.")]
        [:label {:class '[flex items-center gap-2]}
         "Background color"
         [:select {:class          '[rounded border p-2]
                   :data-bind      (signal-name :tab/background-color)
                   :data-action    (set-background-color-path)
                   :data-on:change "@post(el.dataset.action)"}
          (for [color background-colors]
            [:option {:value (name color)} (name color)])]]
        [:a {:class '[text-blue-600 hover:underline] :href (routes/admin)}
         "Admin dashboard"]
        [:form {:method "post" :action (routes/signout)}
         [:button {:class '[text-blue-600 hover:underline] :type "submit"}
          "Sign out"]]]))))

(def module
  {:biff.ring/routes
   ["" {:middleware [mid/wrap-signed-in]}
    [(routes/app) {:get demo-page}]
    [(increment-clicks-path) {:post increment-clicks}]
    [(set-background-color-path) {:post set-background-color}]]})
