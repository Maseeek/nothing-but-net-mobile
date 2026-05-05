# Implementation Plan - Lecture Knowledge Extraction

This plan outlines the process for extracting and synthesizing coding practices and requirements from the module lectures and coursework specification.

## Goal
To create a comprehensive understanding of the "Loughborough University style" of Android development as taught in 25COB155, ensuring that all subsequent code adheres to these specific practices and university standards.

## User Review Required
> [!IMPORTANT]
> The coursework specification mentions that GenAI usage is permitted for idea generation and debugging, but NOT for design, implementation, or evaluation. I will focus on being an "assistive agent" that helps you implement your ideas according to the lecture standards.

## Proposed Changes

### [Phase 01 - Planning]

#### [NEW] [coding-standards.md](file:///c:/Users/masee/OneDrive%20-%20Loughborough%20University/UNIVERSITY/Mobile%20App%20Dev/nothing-but-net-mobile/phases/01-planning/coding-standards.md)
This file will serve as the "brain" for coding style, containing:
- Kotlin conventions (val vs var, type inference, etc.)
- UI standards (Jetpack Compose or XML? The lectures will tell me).
- Architecture preferences (ViewModel, Coroutines).
- Specific requirements from the CW spec.

## Extraction Strategy
1. **Iterate through PDFs**: I will use the `view_file` tool to read each of the 11 lecture PDFs.
2. **Identify Patterns**:
   - Component declaration style.
   - Resource management (strings.xml vs hardcoded).
   - Lifecycle handling patterns.
   - Database/Local storage implementation (Room vs raw SQL?).
3. **Synthesize into `coding-standards.md`**: I'll group these by topic (Kotlin, UI, Data, Security, etc.).

## Verification Plan
### Manual Verification
- The user will review the `coding-standards.md` file to ensure it matches their understanding of the module.
