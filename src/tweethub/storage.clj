(ns tweethub.storage
  (:require [clojure.java.io :as io]))

(def app-state (atom {}))

(defn file-exists?
  "Does the provided filename exist?"
  [filename]
  (.exists (io/as-file filename)))

(defn load-state
  "Given a filename describing an EDN file, attempts to load it from disk
  and returns a map of its contents. If the filename does not exist,
  returns a new map."
  [filename]
  (if (file-exists? filename)
    (read-string (slurp filename))
    {}))

(defn new-pull-request?
  "Does the PR exist in the application's state - i.e.,
   has it been posted already?"
  [app-state pull-request]
  (nil? (get @app-state (:id pull-request))))

(defn save-pull-request
  "Adds a new pull request to the application's state."
  [app-state pull-request]
  (swap! app-state assoc (:id pull-request) pull-request))

(defn start-application-state
  "When called, loads the map contained in `filename` (or initializes an empty map)
   to serve as the app's state, merges in those contents to the `app-state` atom,
   and adds a watcher that will persist the current state of the atom to `filename`
   whenever changes are made."
  [filename]
  (let [file-state (load-state filename)]
    (do
      (swap! app-state merge file-state)
      (add-watch app-state :save
                 (fn [key atom old-state new-state]
                   (spit filename (pr-str new-state)))))))
