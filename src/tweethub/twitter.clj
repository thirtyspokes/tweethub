(ns tweethub.twitter
  (:require [tweethub.storage :refer [save-pull-request]]
            [clj-http.client :as client]
            [oauth.client :as oauth]
            [taoensso.timbre :as timbre :refer [info error]]))

(defn create-consumer
  "Creates an OAuth consumer with a signing algorithm of HMAC-SHA1
   per Twitter's OAuth documentation."
  [key secret]
  (oauth/make-consumer key
                       secret
                       "https://api.twitter.com/oauth/request_token"
                       "https://api.twitter.com/oauth/access_token"
                       "https://api.twitter.com/oauth/authorize"
                       :hmac-sha1))

(defn fetch-credentials
  "Given the configuration and desired status to post, create and sign the message
   using the access token for this app account."
  [config tweet-body]
  (oauth/credentials (create-consumer (:twitter-oauth-key config) (:twitter-oauth-secret config))
                     (:twitter-access-token config)
                     (:twitter-access-secret config)
                     :POST
                     "https://api.twitter.com/1.1/statuses/update.json"
                     tweet-body))

(defn build-tweet-text
  "Constructs a message to serve as the body of a tweet."
  [{:keys [user title number url]}]
  (format "%s just opened PR #%s: \"%s\" - view more: %s" user number title url))

(defn post-tweet
  "Given a config map and an issue from Github, generates a signed
   OAuth request body and POSTs it to the Twitter Status API. If successful,
   adds the issue to the app state as posted.

   If an exception is raised when attempting to post to Github, the error is
   logged and the PR isn't stored (which will allow it to be tried again)."
  [app-state config pull-request]
  (let [tweet-text (build-tweet-text pull-request)
        tweet-body {:status tweet-text}]
    (try
      (client/post "https://api.twitter.com/1.1/statuses/update.json"
                   {:query-params (merge (fetch-credentials config tweet-body) tweet-body)
                    :cookie-policy :standard})
      (info (format "Successfully posted PR #%s to Twitter." (:number pull-request)))
      (save-pull-request app-state pull-request)
      (catch Exception e (error (format "Failed to post PR to twitter: %s" (.getMessage e)))))))
