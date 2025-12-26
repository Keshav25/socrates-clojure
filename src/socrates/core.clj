(ns socrates.core
  "Core orchestration for Socrates AI assistant.
   
   Responsibilities:
   - Component initialization
   - Lifecycle management
   - Main event loop"
  (:require [socrates.ollama :as ollama]
            [socrates.rag :as rag]
            [socrates.context :as context]
            [socrates.mcp :as mcp]
            [socrates.emacs :as emacs]
            [socrates.speech :as speech]
            [clojure.core.async :as async]))

(def system-state (atom {:running false
                         :components {}}))


(defn init
  "Initializes all components.
   
   Returns:
   - System state map"
  []
  (println "Initializing Socrates...")
  (let [state {:running false
               :components {:ollama :ready
                           :rag :ready
                           :context :ready
                           :mcp :ready
                           :emacs :ready
                           :speech :ready}}]
    (reset! system-state state)
    (emacs/connect)
    (rag/load-index "data/index/rag-index.edn")
    state))

(defn start
  "Starts the system.
   
   Returns:
   - Updated system state"
  []
  (swap! system-state assoc :running true)
  (println "Socrates started")
  @system-state)

(defn stop
  "Stops the system gracefully."
  []
  (println "Stopping Socrates...")
  (rag/save-index "data/index/rag-index.edn")
  (swap! system-state assoc :running false)
  (println "Socrates stopped"))

