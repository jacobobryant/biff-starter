(ns com.example.modules
  (:require [com.biffweb.background :as biff.background]
            [com.biffweb.config :as biff.config]
            [com.biffweb.datastar :as biff.datastar]
            [com.biffweb.fx :as biff.fx]
            [com.biffweb.graph :as biff.graph]
            [com.biffweb.ring :as biff.ring]
            [com.biffweb.sqlite :as biff.sqlite]
            [com.example.app.admin :as admin]
            [com.example.app.archive :as archive]
            [com.example.app.auth :as auth]
            [com.example.app.landing :as landing]
            [com.example.app.todos :as todos]
            [com.example.model.schema :as schema]
            [com.example.model.tab-state :as model.tab-state]
            [com.example.model.todo :as model.todo]
            [com.example.model.user :as model.user]))

;; TODO use consistent ns aliases: app.*, model.*

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
   model.tab-state/module
   model.todo/module
   schema/module
   admin/module
   landing/module
   auth/module
   archive/module
   todos/module])

(def start-order
  [:biff.config/module
   :biff.sqlite/module
   :biff.admin/module
   :biff.background/module
   :biff.ring/module])
