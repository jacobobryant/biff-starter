(ns com.example
  (:require [com.biffweb.core :as biff.core]
            [com.biffweb.datastar :refer [disconnect]]
            [com.biffweb.graph :as biff.graph]
            [com.example.modules :refer [modules start-order]]
            [nrepl.cmdline :as nrepl])
  (:gen-class))

(defonce system (atom {}))

;; Causes clients to restart their SSE connections after files are evaluated.
(disconnect @system)

(defn start []
  (reset! system (biff.core/start #'modules start-order))
  (spit "graph.mmd" (biff.graph/uml @system))
  :started)

(defn stop []
  (biff.core/stop @system)
  (reset! system {})
  :stopped)

(defn -main [& _args]
  (start)
  (let [{:biff.tasks/keys [nrepl-port]} @system]
    (.addShutdownHook (Runtime/getRuntime) (Thread. #'stop))
    (nrepl/-main "--port" nrepl-port
                 "--middleware" (pr-str '[cider.nrepl/cider-middleware]))))
