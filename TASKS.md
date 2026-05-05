# Project Tasks: Nothing But Net

## 🏁 Phase 1: Planning & Design
# Project Tasks: Nothing But Net

## 🏁 Phase 1: Planning & Design
- [x] Initial Project Setup <!-- id: 0 -->
- [x] Define Core Features and Architecture <!-- id: 1 -->
- [x] Create Project Outline in `resources/` <!-- id: 2 -->
- [x] Finalize UI/UX Wireframes <!-- id: 3 -->
- [x] Define API Contract with Backend <!-- id: 4 -->

## 🛠 Phase 2: Implementation (Mobile MVP)
- [x] Initialize Android Project Structure (`src/`) <!-- id: 5 -->
- [x] Implement Authentication Flow <!-- id: 6 -->
- [x] Build Main Dashboard (Compose) <!-- id: 7 -->
    - [x] Implement Dashboard Header with User Greeting <!-- id: 14 -->
    - [x] Create Live Session Status Card (Stats + Controls) <!-- id: 15 -->
    - [x] Implement Quick Stats Grid (Total Shots, Streak, Avg Angle) <!-- id: 16 -->
    - [x] Design AI Training Insights Component <!-- id: 17 -->
    - [x] Build Arc Analysis Graph using Canvas/Library <!-- id: 18 -->
    - [x] Configure Bottom Navigation Bar with custom FAB (Home, Analysis, Record, History, Profile) <!-- id: 19 -->
- [x] Migrate and rename brand assets to standard Android structure <!-- id: 20 -->
- [ ] Integrate Camera and Video Upload <!-- id: 8 -->
- [ ] Add Shot Analysis Visualization <!-- id: 9 -->

## 🧪 Phase 3: Testing & Polish
- [ ] Unit Testing for ViewModels <!-- id: 10 -->
- [ ] UI Testing for Core Flows <!-- id: 11 -->
- [ ] Performance Optimization <!-- id: 12 -->
- [ ] Final Spec Adherence Check <!-- id: 13 -->

---

## 🧠 AGENT MEMORY (Current State)

**Last Updated**: 2026-05-05
**Current Phase**: Phase 2 (Implementation)

### Accomplishments
- Established the ICM (Folders as State) architecture.
- Defined the "Thin Client" architecture for the basketball analysis app.
- Created root `README.md` and initial `TASKS.md`.
- Updated root `README.md` with correct Android Studio import instructions.
- Scaffolded Android Jetpack Compose project with Clean Architecture folders (`data/`, `domain/`, `ui/`) in the `app/` module.
- Added dependencies: Hilt, Room, Retrofit, Navigation Compose, Coroutines.
- Created base `HomeScreen` and `HomeViewModel` to verify setup.
- Committed and pushed changes to the private `origin` repository.
- Implemented core dashboard UI components matching the mockup (`dashboard-mockup.png`).
- Updated project theme with brand colors (Orange/Dark) and disabled dynamic colors for brand consistency.
- Created modular UI components: `StatCard`, `LiveSessionCard`, `AIInsightsCard`, `ArcAnalysisGraph`, `DashboardHeader`, and `BottomNavigationBar`.
- Migrated brand assets from `resources/` to `app/src/main/res/drawable` and `app/src/main/assets`, renaming them to follow Android `snake_case` conventions.
- Implemented Authentication Flow ported from Node.js/TS backend:
  - Created `AuthApi` Retrofit interface.
  - Implemented `AuthRepositoryImpl` and `TokenManager` using SharedPreferences.
  - Created `AuthViewModel` with `StateFlow` for state management.
  - Built `LoginScreen` matching the dark/orange brand aesthetic.
  - Integrated `AppNavigation` to route users based on authentication status.
- Implemented brand logo across the app (Splash, Login, Header).
- Adjusted the vertical position of the Record FAB in the BottomNavigationBar for improved UI alignment.
- Updated `LoadingScreen` to show the `nbn_light` icon and `nbn_logo_transparent` text side-by-side, and `DashboardHeader` to use the `nbn_light` asset.


### Pending
- Integrate Camera and Video Upload functionality.
- Add real-time Shot Analysis Visualization on top of video feed.
