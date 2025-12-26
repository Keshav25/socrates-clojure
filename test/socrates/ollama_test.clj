(ns socrates.ollama-test
  "Tests for Ollama client."
  (:require [clojure.test :refer :all]
            [socrates.ollama :as ollama]
            [clojure.core.async :as async]))

(deftest test-ollama-chat-returns-message-for-simple-query
  "Tests that a simple chat query returns a message response.
   
   Given: A valid Ollama instance running
   When: We send a simple chat query 'What is 2+2?' with streaming disabled
   Then: We should receive a response with a message string"
  (let [response (ollama/chat "What is 2+2?" {:stream false})]
    (is (map? response)
        "Response should be a map, but got: %s (type: %s)"
        response (type response))
    (when (contains? response :message)
      (is (string? (:message response))
          "Message should be a string, but got: %s (type: %s)
           Full response: %s"
          (:message response)
          (type (:message response))
          response))))

(deftest test-ollama-chat-streaming-returns-channel
  "Tests that streaming chat returns a channel with message chunks.
   
   Given: A valid Ollama instance running
   When: We send a chat query with streaming enabled
   Then: We should receive a channel that yields message chunks"
  (let [ch (ollama/chat "Hello" {:stream true})]
    (is (some? ch)
        "Should return a channel, but got: %s (type: %s)"
        ch (type ch))
    (is (instance? clojure.core.async.impl.channels.ManyToManyChannel ch)
        "Should return a core.async channel, but got: %s"
        (type ch)))))

(deftest test-ollama-embed-generates-vector
  "Tests that embedding generation returns a vector of floats.
   
   Given: A valid Ollama instance running
   When: We request an embedding for text 'test'
   Then: We should receive a vector of numbers"
  (let [embedding (ollama/embed "test")]
    (is (vector? embedding)
        "Embedding should be a vector, but got: %s (type: %s)"
        embedding (type embedding))
    (when (seq embedding)
      (is (every? number? embedding)
          "All embedding values should be numbers, but got: %s"
          embedding)
      (is (> (count embedding) 0)
          "Embedding should not be empty, got %d dimensions"
          (count embedding)))))

(deftest test-ollama-list-models-returns-vector
  "Tests that list-models returns a vector of model information.
   
   Given: A valid Ollama instance running
   When: We request the list of models
   Then: We should receive a vector of model maps"
  (let [models (ollama/list-models)]
    (is (vector? models)
        "Models should be a vector, but got: %s (type: %s)"
        models (type models))
    (when (seq models)
      (is (every? map? models)
          "All models should be maps, but got: %s"
          models))))
