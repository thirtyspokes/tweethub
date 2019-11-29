(ns tweethub.core
  (:require [tweethub.github :as github]
            [tweethub.storage :as storage]
            [taoensso.timbre :as timbre :refer [info]]
            [clojure.core.async :refer [go-loop]]))

(defn run
  [state config]
  (Thread/sleep 10000)
  (info "Polling for pull requests...")
  (github/process-pull-requests state config))

(defn -main
  [& args]
  (let [config (read-string (slurp "config.edn"))
        app-state (storage/start-application-state (:app-state-filename config))]
    (go-loop []
      (run app-state config)
      (recur)))
  (while true))
