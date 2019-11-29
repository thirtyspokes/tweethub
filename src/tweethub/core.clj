(ns tweethub.core
  (:require [tweethub.github :as github]
            [tweethub.storage :as storage]
            [taoensso.timbre :as timbre :refer [info]]
            [clojure.core.async :refer [go-loop]]))

(defn -main
  [& args]
  (let [config (read-string (slurp "config.edn"))
        app-state (storage/start-application-state (:app-state-filename config))]
    (go-loop []
      (info "Polling for pull requests...")
      (github/process-pull-requests app-state config)
      (Thread/sleep 10000)
      (recur)))
  (while true))
