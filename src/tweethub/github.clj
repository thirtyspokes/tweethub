(ns tweethub.github
  (:require [tweethub.twitter :refer [post-tweet]]
            [tweethub.storage :refer [new-pull-request?]]
            [cheshire.core :refer :all]
            [clj-http.client :as client]
            [taoensso.timbre :as timbre :refer [info]]
            [clojure.core.reducers :as r]))

(defn get-pull-requests
  "Fetches the latest PRs for the `username`'s `repo` repository, and returns
   the JSON as a seq of maps."
  [username repo]
  (let [url (format "https://api.github.com/repos/%s/%s/pulls" username repo)]
    (parse-string (:body (client/get url)) true)))

(defn parse-pull-requests
  "Transforms the JSON structures from the github API to maps."
  [pull-requests]
  (map (fn [pr] {:id (:id pr)
                 :user (get-in pr [:user :login])
                 :title (:title pr)
                 :number (:number pr)
                 :url (:html_url pr)})
       pull-requests))

(defn process-new-pull-request
  "Checks to see if the issue exists in the application state already; if so,
  skips it and does nothing, otherwise will post the issue to twitter."
  [app-state config pull-request]
  (if-not (new-pull-request? app-state pull-request)
    (info (format "PR #%s is already posted, skipping." (:number pull-request)))
    (do
      (info (format "PR #%s is new! Posting to Twitter." (:number pull-request)))
      (post-tweet app-state config pull-request))))

(defn process-pull-requests
  "Parses the PRs from Github, and then for each PR that does not already
   exist in the application state, posts it to Twitter."
  [app-state {:keys [github-user github-repo] :as config}]
  (let [parsed-prs (parse-pull-requests (get-pull-requests github-user github-repo))]
    (doall (pmap #(process-new-pull-request app-state config %) parsed-prs))))
