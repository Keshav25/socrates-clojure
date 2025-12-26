(ns socrates.mcp-test
  "Tests for MCP client."
  (:require [clojure.test :refer :all]
            [socrates.mcp :as mcp]))

(deftest test-register-tool-adds-to-tools
  "Tests that registering a tool adds it to the tools map.
   
   Given: No tools registered
   When: We register a test tool
   Then: The tool should be available in the tools list"
  (let [test-tool-name "test_tool"
        _ (mcp/register-tool test-tool-name "Test tool" (fn [args] "result"))
        tools (mcp/list-tools)]
    (is (some #(= test-tool-name (:name %)) tools)
        "Tool should be in tools list. Available tools: %s"
        (map :name tools))))

(deftest test-execute-tool-returns-result
  "Tests that executing a tool returns a result.
   
   Given: A registered tool
   When: We execute the tool
   Then: We should get a result with success status"
  (let [test-file (java.io.File/createTempFile "mcp-test" ".txt")
        _ (spit test-file "test content")
        result (mcp/execute-tool "read_file" {:path (.getAbsolutePath test-file)})]
    (is (:success result)
        "Tool execution should succeed. Result: %s"
        result)
    (is (contains? result :result)
        "Result should contain :result key. Result: %s"
        result)
    (.delete test-file)))
