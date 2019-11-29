(ns tweethub.core
  (:require [tweethub.github :as github]
            [tweethub.storage :as storage]
            [taoensso.timbre :as timbre :refer [info error]]
            [clojure.core.async :refer [go-loop]]))

(defn load-config
  []
  (try
    (read-string (slurp "config.edn"))
    (catch Exception e
      (error "Couldn't load configuration from config.edn - please ensure it is present in the root of the project!")
      (System/exit 1))))

(defn -main
  [& args]
  (let [config (load-config)
        app-state (storage/start-application-state (:app-state-filename config))]
    (go-loop []
      (info "Polling for pull requests...")
      (github/process-pull-requests app-state config)
      (Thread/sleep 10000)
      (recur)))
  (while true))
