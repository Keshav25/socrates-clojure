#!/bin/bash
# Tangle socrates.org using org-babel
# Designed for TDD + TCR workflows - fast and reliable

set -e

ORG_FILE="socrates.org"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f "$ORG_FILE" ]; then
    echo "Error: $ORG_FILE not found in $(pwd)" >&2
    exit 1
fi

echo "Tangling $ORG_FILE..."

# Use emacs batch mode to tangle
emacs --batch \
    --eval "(require 'org)" \
    --eval "(require 'ob-tangle)" \
    --eval "(find-file \"$ORG_FILE\")" \
    --eval "(org-babel-tangle)" \
    --eval "(kill-buffer)"

if [ $? -eq 0 ]; then
    echo "✓ Tangling complete"
    
    # Verify key files were created
    echo ""
    echo "Verifying generated files:"
    
    FILES=(
        "src/socrates/ollama.clj"
        "src/socrates/rag.clj"
        "src/socrates/context.clj"
        "src/socrates/mcp.clj"
        "src/socrates/emacs.clj"
        "src/socrates/speech.clj"
        "src/socrates/core.clj"
        "test/socrates/ollama_test.clj"
        "test/socrates/rag_test.clj"
        "test/socrates/context_test.clj"
        "test/socrates/mcp_test.clj"
        "test/socrates/emacs_test.clj"
        "test/socrates/speech_test.clj"
        "test/socrates/core_test.clj"
    )
    
    MISSING=0
    for file in "${FILES[@]}"; do
        if [ -f "$file" ]; then
            echo "  ✓ $file"
        else
            echo "  ✗ $file (missing)"
            MISSING=$((MISSING + 1))
        fi
    done
    
    if [ $MISSING -eq 0 ]; then
        echo ""
        echo "✓ All expected files generated"
        exit 0
    else
        echo ""
        echo "⚠ Warning: $MISSING file(s) missing"
        exit 1
    fi
else
    echo "✗ Tangling failed" >&2
    exit 1
fi

