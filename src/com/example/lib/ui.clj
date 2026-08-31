(ns com.example.lib.ui
  (:require [clojure.java.io :as io]
            [dev.onionpancakes.chassis.core :as chassis]
            [ring.util.response :as ring-response]))

(def default-page-opts
  {:ui/title "My Application"
   :ui/description nil
   :ui/lang "en"
   :ui/image nil
   :ui/icon nil})

(def ^:private datastar-script-url
  "https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")

(defn static-path [path]
  (if-some [last-modified (some-> (io/resource (str "public" path))
                                  ring-response/resource-data
                                  :last-modified
                                  (.getTime))]
    (str path "?t=" last-modified)
    path))

(defn html-response [body]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (chassis/html [chassis/doctype-html5 body])})

;; TODO convert the inline styles to tailwind classes
(defn page [opts & contents]
  (let [{:ui/keys [title description lang image icon]}
        (merge default-page-opts opts)]
    (html-response
     [:html {:lang lang
             :style {:min-height "100%" :height "auto"}}
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
       (when title
         [[:title title]
          [:meta {:content title :property "og:title"}]])
       (when description
         [[:meta {:name "description" :content description}]
          [:meta {:property "og:description" :content description}]])
       (when image
         [[:meta {:content image :property "og:image"}]
          [:meta {:content "summary_large_image" :name "twitter:card"}]])
       (when icon
         [:link {:rel "icon"
                 :type "image/png"
                 :sizes "16x16"
                 :href icon}])
       [:link {:rel "stylesheet" :href (static-path "/css/main.css")}]
       [:script {:src (static-path "/js/main.js")}]
       [:script {:type "module" :src datastar-script-url}]]
      [:body
       {:style {:position "absolute"
                :width "100%"
                :min-height "100%"
                :display "flex"
                :flex-direction "column"}}
       contents]])))

;; TODO add an app-page function which wraps page and adds datastar stuff (like
;; todos.clj::raw-app-page)

(defn on-error [{:keys [status] :as ctx}]
  (page
   ctx
   [:h1
    (if (= status 404)
      "Page not found."
      "Something went wrong.")]))
