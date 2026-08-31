;; A scratch space for inspecting things with the REPL. You may want to add this
;; to .gitignore.
(ns repl
  (:require [com.example :as main]
            [com.biffweb.sqlite :refer [execute]]))

(defn get-ctx []
  @main/system)

(comment

  (execute (get-ctx) "select * from user")

  )
