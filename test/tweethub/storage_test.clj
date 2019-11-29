(ns tweethub.storage-test
  (:require [clojure.test :refer :all]
            [tweethub.storage :refer :all]))

(deftest new-pull-request-test
  (testing "identifies whether a PR already exists in the app state"
    (is (= true (new-pull-request? (atom {}) {:id 1})))
    (is (= true (new-pull-request? (atom {2 {:id 2}}) {:id 1})))
    (is (= false (new-pull-request? (atom {1 {:id 1}}) {:id 1})))))
