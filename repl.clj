;; A scratch space for inspecting things with the REPL.
(ns repl
  (:require [clojure.tools.namespace.repl :as tn-repl]
            [com.biffweb.sqlite :refer [execute]]
            [com.example :as main]))

(defn get-ctx []
  @main/system)

(defn refresh []
  (main/stop)
  (tn-repl/refresh :after `main/start)
  :done)

(comment

  ;; Only needed if you change code that only runs at startup.
  (refresh)

  (execute (get-ctx) "select * from user"))
