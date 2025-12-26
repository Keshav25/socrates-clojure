(ns socrates.emacs-test
  "Tests for Emacs integration."
  (:require [clojure.test :refer :all]
            [socrates.emacs :as emacs]))

(deftest test-connect-attempts-connection
  "Tests that connect attempts to connect to Emacs.
   
   Note: This test may fail if Emacs is not running.
   It tests the connection attempt, not success."
  (let [result (emacs/connect)]
    (is (boolean? result)
        "Connect should return a boolean, but got: %s (type: %s)"
        result (type result))))
