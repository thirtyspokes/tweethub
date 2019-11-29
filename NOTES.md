Implementation Notes
====================
This was a fun exercise! Reading the description, my first instinct would have been to implement this has a standalone webservice with an API and its own storage via some kind of database, so that we can leverage Github's Webhooks API.  Since we want to optimize for ease of running on someone else's computer, I opted to track the state of seen PRs in an atom that backs itself up to a file.  This felt like a nicely simple option for persisting the state of seen pull requests between application runs without having to assume much about the execution environment of the app.

Another option (and my first choice) would have been to containerize the entire thing and track the state of PRs in a mysql container; however, this presumes that you're running docker locally which might be an imposition.  In the "real world" a database storage solution would let us potentially run multiple instances of the app for scale and we also won't need to mount a storage volume (if we're using containers) for the file to write to.

I opted to provide secrets and other configuration via reading a file so as not to push any tokens to Github; in the "real world" this would be better as reading from environment variables so that it can be easily run in a container.

I grabbed a few dependencies that I use in almost all of my projects:

* Cheshire, for a nice and simple JSON parsing solution (for working with the Github API).
* clj-http, which is my preferred HTTP client library for working in Clojure.  It's reasonably performant and adheres strictly to the Ring spec, so I like how consistent it feels with the rest of the Clojure HTTP ecosystem.
* timbre for logging - I mainly use this one as it absolves me from having to muck around with any log4j properties configuration or anything like that.
* core.async is probably not required at all, I mainly pull it in for `go-loop` for the application's main thread.
* Finally, I use `clj-oauth` for authenticating to the Twitter API.  Because we're authenticating in an app access token context (i.e., we don't have to request an OAuth token and then exchange it for an access token because we are alrady in an authenticated user's context) this library is basically just used for generating the HMAC-SHA1 signature that needs to be supplied so hopefully it doesn't fall too far within "using an SDK."

In terms of overall architecture, we basically poll for PRs from the Github API for the configured repository, and for each one that we haven't already stored a record of, post it to Twitter.  If the tweet is posted successfully, add it to the application state so it won't be posted again.  This might actually be strictly unnecessary because the Twitter Status API will reject new statuses with a 403 if the content matches an existing tweet within a certain timeframe!

The main application loop could probably be improved to use reducers rather than `pmap` to avoid intermediate collections if we've got a ton of PRs to chew through, but it felt like overkill for this use case (we would need to refactor slightly to probably filter seen PRs and then processing that collection instead of doing it in a single step) and with enough parallelism we are likely to get rate-limited by Twitter anyway!