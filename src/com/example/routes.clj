(ns com.example.routes
  (:require [com.biffweb.ring :refer [defpath]]))

(defpath home "/")
(defpath app "/app")
(defpath signin "/signin")
(defpath auth-signout "/_biff/auth/signout")
