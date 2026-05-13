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
- [x] Implement Authentication Flow (Real API, Stable Navigation, and Logout) <!-- id: 6 -->
- [x] Build Main Dashboard (Compose) <!-- id: 7 -->
    - [x] Implement Dashboard Header with User Greeting <!-- id: 14 -->
    - [x] Create Live Session Status Card (Stats + Controls) <!-- id: 15 -->
    - [x] Implement Quick Stats Grid (Total Shots, Streak, Avg Angle) <!-- id: 16 -->
    - [x] Design AI Training Insights Component <!-- id: 17 -->
    - [x] Build Arc Analysis Graph using Canvas/Library <!-- id: 18 -->
    - [x] Configure Bottom Navigation Bar with custom FAB (Home, Analysis, Record, History, Profile) <!-- id: 19 -->
- [x] Migrate and rename brand assets to standard Android structure <!-- id: 20 -->
- [x] Integrate CV and Auth Server Infrastructure (Local & Prod) <!-- id: 21 -->
- [x] Integrate Camera and Video Upload (CameraX Implementation and Gallery Selection Done) <!-- id: 8 -->
- [x] Add Shot Analysis Visualization <!-- id: 9 -->
- [x] Implement Session History and Profile Integration <!-- id: 22 -->
    - [x] Implemented HistoryViewModel to fetch all past sessions from StatsRepository.
    - [x] Built a premium HistoryScreen with a scrollable list of session cards showing date, FG%, and streaks.
    - [x] Updated ProfileScreen to include a "RECENT SESSIONS" section displaying the last 3 sessions.
    - [x] Added navigation from Profile to History via a "View All" link.
    - [x] Integrated real database data into Profile charts (LineChart and DonutChart) and session items.

## 🧪 Phase 3: Testing & Polish
- [ ] Unit Testing for ViewModels <!-- id: 10 -->
- [ ] UI Testing for Core Flows <!-- id: 11 -->
- [ ] Performance Optimization <!-- id: 12 -->
- [ ] Final Spec Adherence Check <!-- id: 13 -->

---

## 🧠 AGENT MEMORY (Current State)

**Last Updated**: 2026-05-13
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
- Implemented Authentication Flow ported from Node.js/TS backend.
- Implemented brand logo across the app (Splash, Login, Header).
- Adjusted the vertical position of the Record FAB in the BottomNavigationBar for improved UI alignment.
- Refined `LoadingScreen` to show only the `nbn_logo_transparent` brand logo centered, removing the icon as per user feedback for a cleaner look.
- Created `deploy.bat` utility script for CLI deployment.
- **Server Integration & Environment Configuration**:
  - Created `NetworkConfig` with a toggle for `LOCAL` vs `PRODUCTION` environments.
  - Configured `NetworkModule` with separate Retrofit instances for Auth (`nothing-but-net.onrender.com`) and CV (`nothing-but-net-cv.onrender.com`) using Hilt Qualifiers.
  - Implemented `CVApi`, `CVModels`, and `CVRepository` for basketball shot analysis via multipart video upload.
  - Bound `CVRepository` in `RepositoryModule` for dependency injection.
- **Functional Login System**:
  - Removed dummy "maseeek" login logic to enable real API authentication via Render servers.
  - Refactored `AppNavigation.kt` to use `LaunchedEffect` for stable navigation transitions.
  - Implemented a functional "Logout" button in the `ProfileScreen`.
  - Added automatic redirect from Registration to Login upon success.
  - Replaced text-based branding in Auth screens with official `nbn_light` and `nbn_logo_transparent` assets.

- **CameraX & Video Management Integration**:
  - Added CameraX dependencies to `libs.versions.toml` and `app/build.gradle.kts`.
  - Configured `AndroidManifest.xml` with `CAMERA`, `RECORD_AUDIO`, and `READ_MEDIA_VIDEO` permissions.
  - Implemented `RecordScreen.kt` using CameraX's `VideoCapture` and `Recorder` for high-quality video capture.
  - **Added Dual-Source Selection**: Implemented a modern selection UI in `RecordScreen` allowing users to choose between "Record" and "Library".
  - Integrated `ActivityResultContracts.GetContent` for gallery video picking.
  - Added a "Back" button to the camera preview for returning to the selection choice.
  - Added real-time recording UI feedback (Red status text and toggleable Record/Stop button).
  - Wrote recorded videos to cache directory with a unique timestamped naming convention.
  - Integrated `RecordScreen` into `AppNavigation.kt`.
- **End-to-End Analysis Workflow**:
  - Wired `RecordScreen` to `AnalysisScreen` via navigation arguments (`videoUri`).
  - Created `FileUtils` to handle `Uri` to `File` conversion for different Android storage schemes.
  - Updated `AnalysisViewModel` to trigger `CVRepository.analyzeVideo` automatically on entry.
  - [x] Built interactive `AnalysisScreen` with real-time status feedback (Loading, Success, Error).
- [x] **Build Fixes (2026-05-13)**:
  - Fixed `PROCESSING_ERROR` in KSP caused by missing `androidx.lifecycle.ViewModel` import in `AnalysisViewModel.kt`.
  - Resolved "Unresolved reference" errors for `CheckCircle`, `Error`, and `ArrowBack` icons by adding missing imports and updating to `AutoMirrored` icons where appropriate.
  - Verified build with `assembleDebug`.
- **Coordinate Selection System**:
  - Implemented interactive hoop coordinate selection on the video thumbnail inside `AnalysisScreen`.
  - Managed UI state for selecting Left and Right edges dynamically.
  - Automatically mapped tap coordinates to actual video frame dimensions using `VideoUtils` before triggering server upload.
  - Added visual "L" and "R" markers (pins) on the video thumbnail to provide real-time feedback of selected coordinates.
- **UI Branding Refinement**:
  - Redesigned `LoginScreen` and `RegisterScreen` to feature a large (240.dp), centered visual logo (icon) at the top of the screen.
  - Removed text-based branding (`nbn_logo_transparent`) from Auth screens for a cleaner, more premium visual identity.
  - Adjusted layout to `TopCenter` alignment to ensure the logo prominently covers the top area.

- **Database Integration & Data Extraction**:
  - Implemented Room database (`AppDatabase`) with `ShotAnalysisEntity` to store basketball session statistics.
  - Created `ShotAnalysisDao` for latest analysis retrieval and session persistence.
  - Developed `StatsRepository` and `StatsRepositoryImpl` to bridge DAO and ViewModels.
  - Updated `CVRepositoryImpl` to automatically save successful analysis results to the local database.
  - Enhanced `HomeViewModel` to fetch real-time stats from the database, replacing hardcoded placeholders.
  - Implemented a database seeding mechanism in `StatsRepository` to ensure the UI has mock data for initial coursework presentation.
- **UI Localization & Hardcoded Data Removal**:
  - Populated `strings.xml` with comprehensive UI resources for Home, Auth, and Profile screens.
  - Refactored `HomeScreen`, `LoginScreen`, `RegisterScreen`, and `ProfileScreen` to use `stringResource` instead of hardcoded strings.
  - Replaced hardcoded stats in `HomeScreen` with dynamic state from `HomeViewModel`.
  - Updated `AIInsightsCard` and `ArcAnalysisGraph` to support dynamic data labels.
- **Security & UX Refinement**:
  - Configured password fields in `LoginScreen` and `RegisterScreen` to disable autocorrect and use `KeyboardType.Password`, ensuring standard mobile security behavior and preventing keyboard suggestions.
  - Fixed "Analysis" navigation icon active state detection in `BottomNavigationBar` by using `startsWith` matching to account for route parameters.
  - Updated branding in `strings.xml` and `DashboardHeader.kt`: changed "Nothing But Net Mobile" to "Nothing But Net" to ensure the header displays the cleaner "nothingbutnet" brand identity.
  - **Coordinate Selection & Analysis Flow (2026-05-13)**:
  - Improved the `AnalysisScreen` workflow by adding a "Confirmation" step; users now select both hoop edges and see markers before triggering the server upload.
  - Implemented the **Shot Analysis Visualization** (id: 9): added a vertical list of shot results to the `AnalysisScreen` success state, displaying shot number, arc angle, and result (swish/miss).
- **Build Fixes (2026-05-13)**:
  - Fixed `PROCESSING_ERROR` in KSP caused by missing `androidx.lifecycle.ViewModel` import in `AnalysisViewModel.kt`.
  - Resolved "Unresolved reference" errors for `CheckCircle`, `Error`, and `ArrowBack` icons by adding missing imports and updating to `AutoMirrored` icons where appropriate.
  - **NEW**: Resolved a major build failure in `AnalysisScreen.kt` by adding missing `kotlinx.coroutines.withContext` and `kotlinx.coroutines.Dispatchers` imports.
  - Verified build with `deploy.bat` (Success).
- **Session History & Profile Integration**:
  - Implemented `HistoryViewModel` to fetch all past sessions from `StatsRepository`.
  - Built a premium `HistoryScreen` with a scrollable list of session cards showing date, FG%, and streaks.
  - Updated `ProfileScreen` to include a "RECENT SESSIONS" section displaying the last 3 sessions.
  - Added navigation from Profile to History via a "View All" link.
  - Ensured all Profile and History data is dynamically pulled from the Room database, including FG% history charts and donut distribution charts.

### Pending
- Unit Testing for ViewModels.
- UI Testing for core flows.
