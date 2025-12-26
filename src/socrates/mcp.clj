(ns socrates.mcp
  "MCP (Model Context Protocol) client.
   
   Responsibilities:
   - MCP server communication
   - Tool discovery and execution
   - Result handling"
  (:require [cheshire.core :as json]))

(def tools (atom {}))


(defn register-tool
  "Registers a tool for MCP execution.
   
   Parameters:
   - name: Tool name
   - description: Tool description
   - handler: Function that executes the tool"
  [name description handler]
  (swap! tools assoc name {:name name
                          :description description
                          :handler handler}))

(defn list-tools
  "Lists all registered tools.
   
   Returns:
   - Vector of tool information maps"
  []
  (vec (vals @tools)))

