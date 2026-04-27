---
name: task-manager
description: Automatically manages the project's TASKS.md file and synchronizes Agent Memory with the phase-based ICM architecture.
---

# 🤖 Task Manager Skill

This skill optimizes the Antigravity workflow by ensuring the project state is always transparent and up-to-date, specifically aligning the global `TASKS.md` tracker with the local `phases/` state documents.

## 📋 Core Protocols

1. **State Synchronization**: 
   - After every significant change or group of tool calls, review [TASKS.md](../../../TASKS.md).
   - Update the relevant checkboxes in `TASKS.md` to reflect task completion.
   - Summarize what was just done in the **AGENT MEMORY (Current State)** section.
   - IMPORTANT: If a major milestone is reached, ensure the local state document in the current active `phases/` directory is also updated using the `state-update` skill.

2. **Dependency Management**: 
   - If a manual configuration step is required (e.g., configuring Android SDK, modifying local properties), add it as a blocked task in `TASKS.md` with the label `[BLOCKED]`.

3. **Contextual Memory**: 
   - Record newly discovered project rules, architecture decisions (like Kotlin standard patterns), or patterns in the "Current State" section to help future agents pick up exactly where you left off.
   - Reference standard rules located in [/.agents/rules/](../rules/).

## 🛠 Usage
This skill is activated automatically when a user asks for a status check, a project refactor, or a feature implementation.

---
> [!NOTE]
> This skill relies on the presence of a `TASKS.md` file in the project root. If missing, create one initialized with the core mobile app development phases aligned with the `phases/` directory structure.
