(ns com.example.app.demo
  (:require
    [com.example.lib.middleware :as mid]
    [com.example.lib.ui :as ui]))

;; TODO
;; - register this module
;; - first do the TODO in ui.clj; this page should be driven by datastar.
;; - add a button that increments :user/n-clicks, with a label saying "This
;;   button has been clicked {n} times." use the authorized-write effect handler
;;   and defpipeline.
;; - add a dropdown that changes the :background-color key in tab state.
;; - again, do this all in biff.datastar style: the action handlers return empty
;; responses (they don't even change signals) and leave rendering to
;; `demo-page`.
;; - add a link to the admin dashboard


(defn demo-page [_request]
  (ui/page
   {}
   "TODO"))

(def module
  {:biff.ring/routes
   ["" {:middleware [mid/wrap-signed-in]}
    ["/app" {:get demo-page}]]})
