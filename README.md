# tweethub

Posts summaries of new pull requests to a particular repository to Twitter.

With the provided configuration, the app will be polling for new pull requests to [thirtyspokes/pr-tracking](https://github.com/thirtyspokes/pr-tracking) and will additionally backfill the current set of open requests.  A polling loop will run every 10 seconds and check for any new PRs, and each new PR will be posted to the configured Twitter account.

The PRs will be posted to the [@tweethubt](https://twitter.com/tweethubt) Twitter account.

## Usage

After cloning the repository, ensure that the provided `config.edn` is in the root of the project directory:

```
$ ls
README.md
config.edn <---
project.clj
resources/
src/
target/
test/
```

It should look like so:

```
$ cat config.edn
{
 :app-state-filename "issues.edn"
 :github-user "thirtyspokes"
 :github-repo "pr-tracking"
 :twitter-oauth-key "OUATH_KEY"
 :twitter-oauth-secret "OAUTH_SECRET"
 :twitter-access-token "ACCESS_TOKEN"
 :twitter-access-secret "ACCESS_SECRET"
}
```

Once you have the config file present, you can run the app via `lein run`.

```
$ lein run
19-11-29 14:55:01 diana.local INFO [tweethub.core:12] - Polling for pull requests...
19-11-29 14:55:02 diana.local INFO [tweethub.github:32] - PR #3 is new! Posting to Twitter.
19-11-29 14:55:03 diana.local INFO [tweethub.twitter:48] - Successfully posted PR #3 to Twitter.
...
```

Run the tests via `lein test`.

Implementation Notes
====================

See [NOTES.md](NOTES.md) for my notes on implementation.