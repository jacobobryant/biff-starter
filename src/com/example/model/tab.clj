(ns com.example.model.tab
  (:require [com.biffweb.graph :refer [defresolver]]))

(def defaults
  {:tab/background-color :white})

(defresolver tab-state
  {:output [{:request/tab [:tab/background-color]}]}

  (fn [{:keys [biff.datastar/tab-id]} _]
    (when tab-id
      [:biff.sqlite.fx/execute
       {:select :*
        :from   :tab-state
        :where  [:= :tab-state/id tab-id]}]))

  (fn [_ [{:tab-state/keys [data]}]]
    {:request/tab (merge defaults data)}))

(def module
  {:biff.graph/resolvers [tab-state]})
