(ns com.example.model.user
  (:require [com.biffweb.graph :refer [defresolver]]))

(defresolver session-user
  {:output [{:session/user [:user/id]}]}
  [{:keys [session]} _]
  (when-some [uid (:uid session)]
    {:session/user {:user/id uid}}))

(def module
  {:biff.graph/resolvers [session-user]})
