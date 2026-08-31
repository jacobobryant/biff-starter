(ns com.example
  (:require [clojure.tools.namespace.repl :as tn-repl]
            [com.biffweb.core :as biff.core]
            [com.example.modules :refer [modules start-order]]
            [nrepl.cmdline :as nrepl])
  (:gen-class))

(defonce system (atom {}))

(defn start []
  (reset! system (biff.core/start #'modules start-order)))

(defn stop []
  (biff.core/stop @system)
  (reset! system {})
  :stopped)

(defn refresh []
  (stop)
  (tn-repl/refresh :after `start)
  :done)

(defn -main [& _args]
  (let [{:biff.tasks/keys [nrepl-port]} (start)]
    (nrepl/-main "--port" nrepl-port
                 "--middleware" (pr-str '[cider.nrepl/cider-middleware]))))
