(ns socrates.ollama
  "Ollama client implementation.
   
   Responsibilities:
   - HTTP communication with Ollama API
   - Streaming response handling
   - Embedding generation
   - Error handling and retries"
  (:require [cheshire.core :as json]
            [clojure.core.async :as async]
            [org.httpkit.client :as http]))

(defn- get-config
  "Loads configuration from config.edn"
  []
  (let [config-file (java.io.File. "resources/config.edn")]
    (if (.exists config-file)
      (read-string (slurp config-file))
      {:ollama {:endpoint "http://localhost:11434"
                :model "llama3.2"
                :timeout 30000}})))

(def config (atom (get-config)))

(defn update-config!
  "Updates the configuration atom"
  [new-config]
  (reset! config new-config))

(defn- ollama-url
  "Constructs the full URL for an Ollama API endpoint"
  [path]
  (str (:endpoint (:ollama @config)) path))

(defn- make-request
  "Makes an HTTP request to Ollama API with error handling"
  [method url body options]
  (let [opts (merge {:timeout (:timeout (:ollama @config))
                     :headers {"Content-Type" "application/json"}}
                    options)
        response @(http/request (merge {:method method
                                        :url url
                                        :body (when body (json/generate-string body))}
                                       opts))]
    (if (>= (:status response) 400)
      (throw (ex-info "Ollama API error"
                      {:status (:status response)
                       :body (:body response)
                       :url url}))
      response)))



(defn chat
  "Sends a chat request to Ollama.
   
   Parameters:
   - prompt: The user's message
   - options: Map with optional keys:
     - :stream (boolean): Whether to stream the response (default: true)
     - :context (vector): Previous conversation context
     - :model (string): Model to use (defaults to config)
   
   Returns:
   - If streaming: channel that yields message chunks
   - If not streaming: map with :message key containing full response"
  ([prompt] (chat prompt {}))
  ([prompt options]
   (let [model (or (:model options) (:model (:ollama @config)))
         stream? (get options :stream true)
         url (ollama-url "/api/chat")
         body {:model model
               :messages (if-let [ctx (:context options)]
                          (conj ctx {:role "user" :content prompt})
                          [{:role "user" :content prompt}])
               :stream stream?}]
     (if stream?
       (let [ch (async/chan)]
         (http/post url
                    {:body (json/generate-string body)
                     :headers {"Content-Type" "application/json"}
                     :timeout (:timeout (:ollama @config))
                     :as :stream}
                    (fn [response]
                      (if (>= (:status response) 400)
                        (async/close! ch)
                        (let [reader (java.io.BufferedReader.
                                      (java.io.InputStreamReader. (:body response)))]
                          ((fn read-loop []
                             (when-let [line (.readLine reader)]
                               (when (not= line "")
                                 (try
                                   (let [data (json/parse-string line true)]
                                     (async/>! ch data)
                                     (when-not (:done data)
                                       (read-loop)))
                                   (catch Exception e
                                     (async/close! ch))))))
                          (async/close! ch)))))
)
         ch)
       (let [response (make-request :post url body {})]
         (json/parse-string (:body response) true))))))



(defn embed
  "Generates an embedding vector for the given text.
   
   Parameters:
   - text: The text to embed
   - options: Map with optional :model key
   
   Returns:
   - Vector of floats representing the embedding"
  ([text] (embed text {}))
  ([text options]
   (let [model (or (:model options) (:model (:ollama @config)))
         url (ollama-url "/api/embeddings")
         body {:model model
               :prompt text}
         response (make-request :post url body {})
         parsed (json/parse-string (:body response) true)]
     (:embedding parsed))))



(defn list-models
  "Lists all available Ollama models.
   
   Returns:
   - Vector of model information maps"
  []
  (let [url (ollama-url "/api/tags")
        response (make-request :get url nil {})
        parsed (json/parse-string (:body response) true)]
    (:models parsed)))
