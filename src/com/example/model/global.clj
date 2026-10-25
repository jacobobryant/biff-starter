(ns com.example.model.global
  (:require [com.biffweb.graph :refer [defresolver]]))

(defresolver users
  {:output [{:global/users [:user/id]}]}
  (fn [_ _]
    {:global/users [:biff.sqlite.fx/execute
                    {:select   [:user/id]
                     :from     :user
                     :order-by [[:user/joined-at :desc]]}]}))

(def module
  {:biff.graph/resolvers [users]})
