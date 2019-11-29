(defproject tweethub "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.9.0"]
                 [clj-http "3.10.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/core.async "0.5.527"]
                 [clj-oauth "1.5.5"]]
  :plugins [[lein-environ "1.1.0"]]
  :main tweethub.core
  :repl-options {:init-ns tweethub.core})
