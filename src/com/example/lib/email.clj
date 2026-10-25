(ns com.example.lib.email
  (:require [clojure.tools.logging :as log]
            [com.biffweb.fx :refer [defpipeline]]))

(defpipeline send-mailersend
  (fn [{:com.example/keys [mailersend-api-key
                           mailersend-from
                           mailersend-from-name
                           mailersend-reply-to]}
       {:keys [to subject html text]}]
    [:biff.fx/http
     {:method           :post
      :url              "https://api.mailersend.com/v1/email"
      :headers          {"Authorization" (str "Bearer " (force mailersend-api-key))}
      :content-type     :json
      :throw-exceptions false
      :as               :json
      :form-params      {:from     {:email mailersend-from :name mailersend-from-name}
                         :reply_to {:email mailersend-reply-to :name mailersend-from-name}
                         :to       [{:email to}]
                         :subject  subject
                         :html     html
                         :text     text}}])

  (fn [_ctx {:keys [status body]}]
    (when (<= 400 status)
      (log/warn "MailerSend error:" body))
    (< status 400)))

(defn send-email [{:com.example/keys [mailersend-api-key] :as ctx}
                  {:keys [to subject text html]}]
  (if mailersend-api-key
    (send-mailersend ctx {:to      to
                          :subject subject
                          :html    html
                          :text    text})
    (do
      (println)
      (println "---")
      (println "To:     " to)
      (println "Subject:" subject)
      (println)
      (println text)
      (println "---")
      (println)
      true)))
