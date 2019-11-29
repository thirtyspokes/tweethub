(ns tweethub.github
  (:require [tweethub.twitter :refer [post-tweet]]
            [cheshire.core :refer :all]
            [clj-http.client :as client]
            [taoensso.timbre :as timbre :refer [info]]))

(defn get-issues
  "Fetches the latest issues for the `username`'s `repo` repository, and returns
   the JSON as a seq of maps."
  [username repo]
  (let [url (format "https://api.github.com/repos/%s/%s/issues" username repo)]
    (parse-string (:body (client/get url)) true)))

(defn parse-issues
  "Transforms the JSON structures from the github API to maps."
  [issues]
  (map (fn [issue] {:id (:id issue)
                    :user (get-in issue [:user :login])
                    :title (:title issue)
                    :number (:number issue)})
       issues))

(defn process-new-issue
  "Checks to see if the issue exists in the application state already; if so,
  skips it and does nothing, otherwise will post the issue to twitter."
  [app-state config issue]
  (if (get @app-state (:id issue))
    (info (format "Issue ID %s is already posted, skipping." (:id issue)))
    (do
      (info (format "Issue ID %s is new! Posting to twitter." (:id issue)))
      (post-tweet config (format "PR #%s was just created by %s: %s" (:number issue) (:user issue) (:title issue)))
      (swap! app-state assoc (:id issue) issue))))

(defn process-issues
  "Processes the new issues (via polling)."
  [app-state {:keys [github-user github-repo] :as config}]
  (let [parsed-issues (parse-issues (get-issues github-user github-repo))]
    (doall (map #(process-new-issue app-state config %) parsed-issues))))
