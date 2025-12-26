(ns socrates.rag
  "RAG (Retrieval Augmented Generation) system.
   
   Responsibilities:
   - Document indexing and chunking
   - Embedding generation and storage
   - Vector similarity search
   - Index persistence"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [socrates.ollama :as ollama]))

(def vector-store (atom {}))

(def embedding-cache (atom {}))

(defn- get-config
  "Loads RAG configuration"
  []
  (let [config-file (java.io.File. "resources/config.edn")]
    (if (.exists config-file)
      (let [config (read-string (slurp config-file))]
        (:rag config))
      {:chunk-size 500
         :chunk-overlap 50
         :top-k 5})))

(def config (atom (get-config)))


(defn cosine-similarity
  "Calculates cosine similarity between two vectors"
  [vec1 vec2]
  (let [dot-product (reduce + (map * vec1 vec2))
        magnitude1 (Math/sqrt (reduce + (map #(* % %) vec1)))
        magnitude2 (Math/sqrt (reduce + (map #(* % %) vec2)))]
    (if (or (zero? magnitude1) (zero? magnitude2))
      0.0
      (/ dot-product (* magnitude1 magnitude2)))))

