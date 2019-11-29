(ns tweethub.twitter
  (:require [clj-http.client :as client]
            [oauth.client :as oauth]))

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
  [config user-params]
  (oauth/credentials (create-consumer (:twitter-oauth-key config) (:twitter-oauth-secret config))
                     (:twitter-access-token config)
                     (:twitter-access-secret config)
                     :POST
                     "https://api.twitter.com/1.1/statuses/update.json"
                     user-params))

(defn post-tweet
  "Given a config map and a desired status for the tweet, generates a signed
   OAuth request body and POSTs it to the Twitter Status API."
  [config status]
  (let [user-params {:status status}]
    (println config)
    (client/post "https://api.twitter.com/1.1/statuses/update.json" 
                 {:query-params (merge (fetch-credentials config user-params) user-params)})))
