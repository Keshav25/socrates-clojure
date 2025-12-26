(ns socrates.core-test
  "Tests for core orchestration."
  (:require [clojure.test :refer :all]
            [socrates.core :as core]))

(deftest test-init-initializes-system
  "Tests that init initializes the system.
   
   Given: System not initialized
   When: We call init
   Then: System state should be initialized"
  (let [state (core/init)]
    (is (map? state)
        "Init should return a state map, but got: %s (type: %s)"
        state (type state))
    (is (contains? state :components)
        "State should contain :components key. State: %s"
        state)))
