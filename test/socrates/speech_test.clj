(ns socrates.speech-test
  "Tests for speech processing."
  (:require [clojure.test :refer :all]
            [socrates.speech :as speech]))

(deftest test-speak-executes-espeak
  "Tests that speak executes espeak command.
   
   Note: This test requires espeak to be installed."
  (let [result (speech/speak "test")]
    (is (map? result)
        "Speak should return a process result map, but got: %s (type: %s)"
        result (type result))))
