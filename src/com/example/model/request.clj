(ns com.example.model.request
  (:require [com.biffweb.graph :refer [defresolver]]
            [com.example.schema :as schema]
            [malli.core :as malli]))

(def tab-state-keys
  (mapv first (malli/entries schema/tab-state-schema)))

(def tab-defaults
  {:tab/background-color :white})

(defresolver tab
  {:output [{:request/tab tab-state-keys}]}

  (fn [{:keys [biff.datastar/tab-id]} _]
    (when tab-id
      [:biff.sqlite.fx/execute
       {:select [:tab-state/data]
        :from   :tab-state
        :where  [:= :tab-state/id tab-id]}]))

  (fn [_ [{:tab-state/keys [data]}]]
    {:request/tab (merge tab-defaults data)}))

(def module
  {:biff.graph/resolvers [tab]})
