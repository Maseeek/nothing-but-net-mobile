# Project Tasks: Nothing But Net

## 🏁 Phase 1: Planning & Design

- [x] Initial Project Setup <!-- id: 0 -->
- [x] Define Core Features and Architecture <!-- id: 1 -->
- [x] Create Project Outline in `resources/` <!-- id: 2 -->
- [x] Finalize UI/UX Wireframes <!-- id: 3 -->
- [x] Define API Contract with Backend <!-- id: 4 -->

## 🛠 Phase 2: Implementation (Mobile MVP)

### Core Systems
- [x] Initialize Android Project Structure (`src/`) <!-- id: 5 -->
- [x] Implement Authentication Flow (Real API, Stable Navigation, and Logout) <!-- id: 6 -->
- [x] Migrate and rename brand assets to standard Android structure <!-- id: 20 -->
- [x] Integrate CV and Auth Server Infrastructure (Local & Prod) <!-- id: 21 -->
- [x] Initialize Room database and StatsRepository <!-- id: 23 -->

### UI & Dashboard
- [x] Build Main Dashboard (Compose) <!-- id: 7 -->
  - [x] Dashboard Header with User Greeting <!-- id: 14 -->
  - [x] Live Session Status Card <!-- id: 15 -->
  - [x] Quick Stats Grid <!-- id: 16 -->
  - [x] AI Training Insights Component <!-- id: 17 -->
  - [x] Arc Analysis Graph <!-- id: 18 -->
  - [x] Custom FAB Bottom Navigation <!-- id: 19 -->
- [x] Refactor Dashboard to show most recent session instead of "Live Session" <!-- id: 36 -->
- [x] Implement fully working Analytics section <!-- id: 37 -->
  - [x] Update `AuthApi` with session and stats endpoints
  - [x] Implement `StatsRepository` sync with Node.js server
  - [x] Remove dummy data seeding from `HomeViewModel`
  - [x] Handle connection errors and display real server data in `AnalysisScreen`
- [x] Implement fully working Settings section <!-- id: 38 -->

### Video Analysis Workflow
- [x] CameraX and Video Selection (Record vs Library) <!-- id: 8 -->
- [x] Hoop Coordinate Selection System <!-- id: 24 -->
- [x] End-to-end CV Analysis Upload <!-- id: 25 -->
- [x] Shot Analysis Result Visualization <!-- id: 9 -->
- [x] Design Results Section for post-processed feedback (Improved Dashboard View) <!-- id: 42 -->

### User History & Stats
- [x] Implement Session History and Profile Integration <!-- id: 22 -->
- [x] Create Leaderboard (sort by shots, FG%, Optimal Arc) <!-- id: 40 -->

### Quality & Audit
- [x] Audit for AI use and compare to coding practices <!-- id: 39 -->
- [x] Final UI consolidation and Best Practices audit <!-- id: 43 -->
- [ ] Final end-to-end testing with server <!-- id: 41 -->

## 🧪 Phase 3: Testing & Polish

- [ ] Unit Testing for ViewModels <!-- id: 10 -->
- [ ] UI Testing for Core Flows <!-- id: 11 -->
- [ ] Performance Optimization <!-- id: 12 -->
- [ ] Final Spec Adherence Check <!-- id: 13 -->

---

## 🧠 AGENT MEMORY (Current State)

**Current Phase**: Phase 2 (Implementation) - Finalizing MVP

### Recent Accomplishments
- **UI Consolidation & Design System Implementation (Task 43)**:
    - **100% Semantic Token Compliance**: Successfully refactored all hardcoded colors, brand constants (`OrangePrimary`), and generic primitives (`Color.White`, `Color.Gray`) across the entire UI package.
    - **MaterialTheme Integration**: All screens (`Home`, `Analysis`, `History`, `Leaderboard`, `Profile`, `Record`, `Settings`, `Login`, `Register`) and shared components now strictly inherit from `MaterialTheme.colorScheme` tokens.
    - **Semantic Overrides**: Implemented `SuccessGreen` (#4CAF50) and `ErrorRed` (#F44336) for logical state visualization (shot makes/misses, error feedback, recording indicators).
    - **Build Stabilization & Validation**: Resolved critical compilation errors related to Composable context in drawing lambdas and missing Hilt imports for `PreferenceManager`. Verified 100% build stability with `./gradlew assembleDebug`.
- **Global Design System**: Standardized "Liquid Glass & Ember" aesthetic with a consistent surface hierarchy (`surfaceVariant` for cards, `onBackground` for primary text, `onSurfaceVariant` for secondary metadata).
- **Leaderboard Implementation**: Created a high-performance leaderboard with multi-criteria sorting and real-time server synchronization.
- **Improved Shot Analysis**: Refactored the Analysis dashboard to prioritize recent practice data and provide detailed session walkthroughs.

### Open Issues & Roadblocks
- **Physical Device Validation**: CameraX recording and CV upload require testing on actual hardware to verify real-world latency and performance. (Currently using Pixel_7 emulator for validation).
- **Environment Setup**: `JAVA_HOME` and `adb` were not in system PATH, causing script fallback. Fixed by starting emulator and providing path-aware commands.
- **Testing Coverage**: Phase 3 (Unit and UI testing) is now ready to begin, focusing on repository logic and core navigation flows.

### Next Steps
1. **Task 41**: Perform final end-to-end verification with the production server.
2. **Phase 3**: Initialize unit tests for `StatsRepository` and ViewModels.

