(ns com.example.app.landing
  (:require [com.example.lib.middleware :as mid]
            [com.example.lib.ui :as ui]
            [com.example.routes :as routes]))

(defn home [_]
  (ui/page
   {}
   [:p
    [:a.text-blue-600.hover:underline {:href (routes/signin)} "Click here"]
    " to sign in."]))

(def module
  {:biff.ring/routes
   ["" {:middleware [mid/wrap-redirect-signed-in]}
    [(routes/home) {:get home}]]})
