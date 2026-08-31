(ns com.example.modules
  (:require [com.biffweb.background :as biff.background]
            [com.biffweb.config :as biff.config]
            [com.biffweb.datastar :as biff.datastar]
            [com.biffweb.fx :as biff.fx]
            [com.biffweb.graph :as biff.graph]
            [com.biffweb.ring :as biff.ring]
            [com.biffweb.sqlite :as biff.sqlite]
            [com.example.app.admin :as app.admin]
            [com.example.app.auth :as app.auth]
            [com.example.app.demo :as app.demo]
            [com.example.app.landing :as app.landing]
            [com.example.lib.ui :as lib.ui]
            [com.example.model.schema :as model.schema]
            [com.example.model.user :as model.user]))

(def modules
  [{:biff.core/init {:biff.ring/on-error #'lib.ui/on-error}}
   (biff.config/module)
   (biff.ring/module)
   (biff.datastar/module)
   (biff.background/module)
   (biff.fx/module)
   (biff.graph/module)
   (biff.sqlite/module)
   model.user/module
   model.schema/module
   app.admin/module
   app.landing/module
   app.auth/module
   app.demo/module])

(def start-order
  [:biff.config/module
   :biff.sqlite/module
   :biff.admin/module
   :biff.background/module
   :biff.ring/module])
