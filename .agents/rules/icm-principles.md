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
7. **Validation-First**: Ensure code is tested (Unit/UI) or manually verified on emulator before state update.
8. **Contextual Persistence**: Log non-trivial bugs, stack traces, and solution hypotheses in `issues.md` to prevent context loss across sessions.
