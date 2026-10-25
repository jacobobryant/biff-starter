(ns com.example.model.session
  (:require [com.biffweb.graph :refer [defresolver]]))

(defresolver user
  {:output [{:session/user [:user/id]}]}
  [{:keys [session]} _]
  (when-some [uid (:uid session)]
    {:session/user {:user/id uid}}))

(def module
  {:biff.graph/resolvers [user]})
