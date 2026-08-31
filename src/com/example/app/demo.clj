(ns com.example.app.demo
  (:require [com.biffweb.datastar :as biff.datastar]
            [com.biffweb.fx :refer [defpipeline]]
            [com.biffweb.sqlite :as biff.sqlite]
            [com.example.lib.middleware :as mid]
            [com.example.lib.ui :as ui]
            [com.example.routes :as routes]))

(def background-colors [:white :red :blue :green])

(defn- demo-state [{:keys [session biff.datastar/tab-id] :as ctx}]
  (let [user      (-> (biff.sqlite/execute
                       ctx
                       {:select [:user/n-clicks]
                        :from   :user
                        :where  [:= :user/id (:uid session)]})
                      first)
        tab-state (when tab-id
                    (-> (biff.sqlite/execute
                         ctx
                         {:select [:tab-state/data]
                          :from   :tab-state
                          :where  [:= :tab-state/id tab-id]})
                        first
                        :tab-state/data))]
    {:n-clicks         (or (:user/n-clicks user) 0)
     :background-color (or (:background-color tab-state) :white)}))

(defpipeline increment-clicks
  (fn [{:keys [session]}]
    {:_ [:biff.sqlite.fx/authorized-write
         {:update :user
          :set    {:user/n-clicks [:+ [:coalesce :user/n-clicks 0] 1]}
          :where  [:= :user/id (:uid session)]}]})

  (fn [_ctx _result]
    {:status 204}))

(defpipeline set-background-color
  (fn [{:biff.datastar/keys [signals tab-id]}]
    (let [background-color (some-> (:demo/background-color signals) keyword)]
      (when-not (contains? (set background-colors) background-color)
        (throw (ex-info "Invalid background color."
                        {:background-color background-color})))
      (when-not tab-id
        (throw (ex-info "Missing Datastar tab ID." {})))
      {:_ [:biff.sqlite.fx/authorized-write
           {:insert-into   :tab-state
            :values        [{:tab-state/id   tab-id
                             :tab-state/data [:lift
                                              {:background-color background-color}]}]
            :on-conflict   [:tab-state/id]
            :do-update-set [:tab-state/data]}]}))

  (fn [_ctx _result]
    {:status 204}))

(defn- background-class [background-color]
  (case background-color
    :red "bg-red-100"
    :blue "bg-blue-100"
    :green "bg-green-100"
    "bg-white"))

(defn demo-page [request]
  (let [{:keys [n-clicks background-color]} (demo-state request)
        color-signal                        (biff.datastar/signal-name
                                             :demo/background-color)]
    (ui/app-page
     request
     {}
     [:main {:id           "demo-page"
             :class        (str "flex flex-1 flex-col items-center justify-center"
                                " gap-4 p-8 " (background-class background-color))
             :data-signals (biff.datastar/signals-json
                            {:demo/background-color background-color})}
      [:button {:type          "button"
                :class         "rounded bg-blue-600 px-4 py-2 text-white"
                :data-on:click (str "@post('" (routes/increment-clicks) "')")}
       (str "This button has been clicked " n-clicks " times.")]
      [:label {:class "flex items-center gap-2"}
       "Background color"
       [:select {:class          "rounded border p-2"
                 :data-bind      color-signal
                 :data-on:change (str "@post('" (routes/set-background-color)
                                      "')")}
        (for [color background-colors]
          [:option {:value (name color)} (name color)])]]
      [:a {:class "text-blue-600 hover:underline" :href (routes/admin)}
       "Admin dashboard"]
      [:form {:method "post" :action (routes/signout)}
       [:input {:name  "__anti-forgery-token"
                :type  "hidden"
                :value (:anti-forgery-token request)}]
       [:button {:class "text-blue-600 hover:underline" :type "submit"}
        "Sign out"]]])))

(def module
  {:biff.ring/routes
   ["" {:middleware [mid/wrap-signed-in]}
    [(routes/app) {:get demo-page}]
    [(routes/increment-clicks) {:post increment-clicks}]
    [(routes/set-background-color) {:post set-background-color}]]})
