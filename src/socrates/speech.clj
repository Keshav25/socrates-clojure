(ns socrates.speech
  "Speech processing (STT and TTS).
   
   Responsibilities:
   - Speech-to-text via Whisper
   - Text-to-speech via espeak"
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]))

(defn- get-config
  "Loads speech configuration"
  []
  (let [config-file (java.io.File. "resources/config.edn")]
    (if (.exists config-file)
      (let [config (read-string (slurp config-file))]
        {:whisper (:whisper config)
         :speech (:speech config)})
      {:whisper {:model-size "tiny"
                 :model-path "/usr/local/bin/whisper.cpp"}
       :speech {:wake-word "socrates"
                :sensitivity 0.5}})))

(def config (atom (get-config)))


(defn speak
  "Synthesizes speech using espeak.
   
   Parameters:
   - text: Text to speak
   
   Returns:
   - Process handle"
  [text]
  (shell/sh "espeak" text))

