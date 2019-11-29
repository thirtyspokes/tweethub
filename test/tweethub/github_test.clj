(ns tweethub.github-test
  (:require [clojure.test :refer :all]
            [tweethub.github :refer :all]))

(def github-json-fixture
  '({:html_url "github.com/1",
     :number 1,
     :state "open",
     :title "Test",
     :id 1
     :user {:node_id "MDQ6VXNlcjk1NDMwODk=",
            :type "User",
            :login "username",
            :id 9543089}},
    {:html_url "github.com/2",
     :number 2,
     :state "open",
     :title "Test2",
     :id 2
     :user {:node_id "MDQ6VXNlcjk1NDMwODk=",
            :type "User",
            :login "username2",
            :id 9543089}}))

(deftest parse-pull-requests-test
  (testing "transforms github API JSON into the desired format"
    (is (= '({:id 1 :user "username" :title "Test" :number 1 :url "github.com/1"}
             {:id 2 :user "username2" :title "Test2" :number 2 :url "github.com/2"})
           (parse-pull-requests github-json-fixture)))))
