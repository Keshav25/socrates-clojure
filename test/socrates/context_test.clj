(ns socrates.context-test
  "Tests for context management."
  (:require [clojure.test :refer :all]
            [socrates.context :as context]))

(deftest test-add-message-adds-to-conversation
  "Tests that adding a message updates the conversation.
   
   Given: An empty conversation
   When: We add a message
   Then: The conversation should contain that message"
  (let [session-id "test-session"
        _ (context/add-message session-id "user" "Hello")
        messages (context/get-conversation session-id)]
    (is (not (empty? messages))
        "Conversation should not be empty after adding message.
         Messages: %s"
        messages)
    (is (= "user" (:role (first messages)))
        "First message should have role 'user', but got: %s"
        (:role (first messages)))
    (is (= "Hello" (:content (first messages)))
        "First message should have content 'Hello', but got: %s"
        (:content (first messages)))))
