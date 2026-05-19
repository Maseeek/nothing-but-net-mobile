# Phase 3: Testing & Polish

## Current State
Working on Quality Assurance and Performance Optimization.

## Completed Tasks
- [x] **Cache Optimization**: 
    - Identified massive cache growth caused by non-deleted video files in `context.cacheDir`.
    - Implemented `FileUtils.getCacheSize` and `FileUtils.clearCache`.
    - Added "Clear Cache" button in `SettingsScreen` with real-time size display.
    - Automated cleanup in `AnalysisViewModel` (after upload) and `AnalysisScreen` (on disposal).
    - Verified build stability after implementation.
- [x] **Home Page Graph Refactoring**:
    - Extracted duplicate `LineChart` implementation from `ProfileScreen.kt` into a shared, reusable `LineChart` component in `ui/components`.
    - Created a new `FgProgressionGraph` component in `ui/components` that styles the shared `LineChart` with clean headers, borders, and margins.
    - Updated `HomeViewModel` to fetch all shot analyses via `getAllShotAnalyses()` to calculate and expose the user's historical field goal percentages over the last 5 sessions (`fgHistory`).
    - Replaced the `ArcAnalysisGraph` component on `HomeScreen` with the new `FgProgressionGraph` component, refactoring the UI.
    - Added corresponding string resources to `strings.xml` for localization.
    - Verified compilation success with `./gradlew assembleDebug`.

## Next Steps
1. Unit Testing for ViewModels (Task 10).
2. UI Testing for Core Flows (Task 11).
3. Final Spec Adherence Check (Task 13).
