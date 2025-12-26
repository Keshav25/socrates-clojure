(ns socrates.emacs
  "Emacs integration via clomacs.
   
   Responsibilities:
   - Emacs connection management
   - Calling Emacs Lisp functions
   - Data format conversion"
  (:require [clomacs.core :as clomacs]))

(def emacs-connected (atom false))


(defn connect
  "Connects to Emacs instance.
   
   Returns:
   - true if connection successful, false otherwise"
  []
  (try
    (clomacs/defn emacs-eval "eval" :private)
    (reset! emacs-connected true)
    true
    (catch Exception e
      (println "Error connecting to Emacs:" (.getMessage e))
      false)))

(defn call-emacs
  "Calls an Emacs Lisp function.
   
   Parameters:
   - function-name: Name of Emacs Lisp function
   - args: Arguments to pass
   
   Returns:
   - Result from Emacs function"
  [function-name & args]
  (if @emacs-connected
    (try
      (clomacs/eval (str "(" function-name " " (pr-str args) ")"))
      (catch Exception e
        {:error (.getMessage e)}))
    {:error "Emacs not connected"}))

