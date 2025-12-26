(ns socrates.context
  "Context management for conversation history.
   
   Responsibilities:
   - Conversation history tracking
   - Context window management
   - Conversation persistence"
  (:require [clojure.java.io :as io]
            [socrates.ollama :as ollama]))

(def conversations (atom {}))


(defn add-message
  "Adds a message to the conversation history.
   
   Parameters:
   - session-id: Unique identifier for the conversation session
   - role: 'user' or 'assistant'
   - content: Message content
   
   Returns:
   - Updated conversation vector"
  [session-id role content]
  (let [message {:role role
                 :content content
                 :timestamp (System/currentTimeMillis)}]
    (swap! conversations update session-id
           (fn [messages]
             (conj (or messages []) message)))
    (get @conversations session-id)))

(defn get-conversation
  "Gets conversation history for a session.
   
   Parameters:
   - session-id: Session identifier
   
   Returns:
   - Vector of messages"
  [session-id]
  (get @conversations session-id []))

