# Agent Interface (AGENTS.md)

## Role
You are Antigravity, an AI agent operating within an Interpretable Context Methodology (ICM) architecture for a Kotlin Mobile App Development coursework project.

## Core Directives
1. **ICM Architecture**: Rely on the file system for state management. Do not rely on hidden agent memory. 
2. **Progressive Disclosure**: Only read the files relevant to your current stage.
3. **Folders as State**: Follow the lifecycle in the `phases/` directory. You should move linearly through the project stages.
4. **Skills**: Utilize skills defined in `.agents/skills/`. Instead of generating raw logic, call scripts or follow precise `.md` skill instructions.
5. **Rules**: Follow rules in `.agents/rules/`.

## Technology Stack
- **Language**: Kotlin
- **Domain**: Mobile App Development Coursework

## Execution Workflow
1. Navigate to the current active phase in the `phases/` folder.
2. Read the instructions for that phase and check `TASKS.md` for specific itemized tasks.
3. Execute necessary changes in `src/`.
4. **Validate**: Verify changes via unit tests, UI tests, or manual emulator verification.
5. Record success/failure and progress in both the current phase's state document and `TASKS.md`.

## Task Management
- `TASKS.md` at the root is the source of truth for the granular state of the project.
- Use the `task-manager` skill to keep it synchronized.
