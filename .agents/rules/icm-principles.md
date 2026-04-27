---
name: icm-principles
description: Enforces Interpretable Context Methodology (ICM) rules across the workspace.
---
# ICM Principles

1. **Grep Over RAG**: Search for yourself using exact lookups (`grep_search`) first.
2. **Schema-First**: Use strict schemas for structured outputs.
3. **Tool Ceiling**: Favor deeper tools over many shallow ones.
4. **State Visibility**: Ensure current step/state is written to the file system (in the `phases/` directories).
5. **Sequential Efficiency**: Prepare checkpoints for human-in-the-loop review.
6. **Self-Correction Loop**: Audit for errors and leftover code, fix, and re-test.
