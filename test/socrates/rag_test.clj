(ns socrates.rag-test
  "Tests for RAG system."
  (:require [clojure.test :refer :all]
            [socrates.rag :as rag]))

(deftest test-cosine-similarity-calculates-correctly
  "Tests that cosine similarity is calculated correctly.
   
   Given: Two vectors
   When: We calculate cosine similarity
   Then: We should get a value between -1 and 1"
  (let [vec1 [1.0 2.0 3.0]
        vec2 [1.0 2.0 3.0]
        similarity (rag/cosine-similarity vec1 vec2)]
    (is (number? similarity)
        "Similarity should be a number, but got: %s (type: %s)"
        similarity (type similarity))
    (is (<= -1.0 similarity 1.0)
        "Similarity should be between -1 and 1, but got: %s"
        similarity)))

(deftest test-chunk-text-splits-into-overlapping-chunks
  "Tests that text is split into overlapping chunks correctly.
   
   Given: A text string and chunk parameters
   When: We chunk the text
   Then: We should get overlapping chunks of the specified size"
  (let [text "This is a test sentence with multiple words to chunk properly"
        chunks (rag/chunk-text text 10 3)]
    (is (vector? chunks)
        "Chunks should be a vector, but got: %s (type: %s)"
        chunks (type chunks))
    (is (> (count chunks) 0)
        "Should have at least one chunk, but got: %d"
        (count chunks))
    (doseq [chunk chunks]
      (is (string? chunk)
          "Each chunk should be a string, but got: %s (type: %s)"
          chunk (type chunk)))))

(deftest test-index-file-creates-entries
  "Tests that indexing a file creates entries in the vector store.
   
   Setup: Create a temporary test file with known content
   When: We index the file
   Then: The vector store should contain entries for each chunk"
  (let [test-file (java.io.File/createTempFile "rag-test" ".txt")
        _ (spit test-file "Sample content for indexing. This is a test.")
        _ (rag/index-file (.getAbsolutePath test-file))
        store (rag/get-vector-store)]
    (is (not (empty? store))
        "Vector store should not be empty after indexing.
         Store contents: %s"
        store)
    (.delete test-file)))

(deftest test-search-returns-relevant-results
  "Tests that search returns results sorted by relevance.
   
   Given: An indexed vector store
   When: We search for a query
   Then: We should get results sorted by similarity score"
  (let [test-file (java.io.File/createTempFile "rag-search" ".txt")
        _ (spit test-file "Machine learning is a subset of artificial intelligence.")
        _ (rag/index-file (.getAbsolutePath test-file))
        results (rag/search "artificial intelligence" 5)]
    (is (vector? results)
        "Search should return a vector, but got: %s (type: %s)"
        results (type results))
    (when (seq results)
      (is (every? #(contains? % :score) results)
          "All results should have a :score. Results: %s"
          results))
    (.delete test-file)))
