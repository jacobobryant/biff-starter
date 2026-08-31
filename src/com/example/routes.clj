(ns com.example.routes
  (:require [com.biffweb.ring :refer [defpath]]))

(defpath home "/")
(defpath app "/app")
(defpath increment-clicks "/app/increment-clicks")
(defpath set-background-color "/app/background-color")
(defpath admin "/_biff/admin")
(defpath signin "/signin")
(defpath signout "/_biff/auth/signout")
