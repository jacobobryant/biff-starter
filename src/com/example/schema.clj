(ns com.example.schema
  (:require [com.biffweb.sqlite.schema :as s :refer [?]]))

(def tab-state-schema
  [:map
   [:tab/background-color ? [:enum :white :red :blue :green]]])

(def columns
  {:tab-state/id   s/pkey
   :tab-state/data (s/edn tab-state-schema)

   :user/id           s/pkey
   :user/email        (s/text :required :unique)
   :user/joined-at    (s/inst :required :index)
   :user/display-name (s/text)})

;; Strings added here will be appended to resources/schema.sql
(def extra-init-sql [])

;; Add new columns here that you want users to be able to edit (e.g. settings).
(def editable-user-fields [:user/display-name])

(defn only-fields-edited? [before after fields]
  (= (apply dissoc before fields)
     (apply dissoc after fields)))

(defn owned-by? [uid owner-key records]
  (every? #(= uid (owner-key %)) (filterv some? records)))

(defn authorize-entry [{{:keys [uid]} :session
                        :keys         [biff.datastar/tab-id]}
                       {:keys [table op before after]}]
  (case table
    :user
    (and (owned-by? uid :user/id [before after])
         (case op
           :create false
           :update (only-fields-edited? before after editable-user-fields)
           :delete true))

    :tab-state
    (owned-by? tab-id :tab-state/id [before after])

    false))

;; These authorization rules apply when using biff.sqlite/authorized-write and
;; are meant as an extra layer of protection.
(defn authorize
  [ctx diff]
  (every? #(authorize-entry ctx %) diff))
