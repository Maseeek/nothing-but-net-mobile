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
  - [x] Replace Arc Analysis Graph with FG% Progression Graph <!-- id: 18 -->
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
- [x] Final end-to-end testing with server <!-- id: 41 -->

## 🏁 Phase 3: Testing & Polish

- [ ] Unit Testing for ViewModels <!-- id: 10 -->
- [ ] UI Testing for Core Flows <!-- id: 11 -->
- [x] Performance Optimization <!-- id: 12 -->
- [x] Final Spec Adherence Check <!-- id: 13 -->
- [x] Custom ContentProvider & Instrumented Tests <!-- id: 14 -->

---

## 🧠 AGENT MEMORY (Current State)

**Current Phase**: Phase 3 (Testing & Polish) - Refactoring & Branding

### Recent Accomplishments
- **Launch Screen Dark Background Fix**:
    - Resolved the issue where the first loading screen (the OS-rendered startup window background) displayed a white background, causing a white-on-white layout clash with the brand logo and a bright flash before Compose initialized.
    - Modified `themes.xml` to inherit from `android:Theme.Material.NoActionBar` (dark theme).
    - Configured `android:windowBackground`, `android:statusBarColor`, and `android:navigationBarColor` to `#121212` in style values to perfectly match the application's Compose `DarkBackground` color.
    - Verified compilation stability with a clean Gradle build.
- **Shot Analysis UI Polish and Card Centering**:
    - Enhanced the `StatCard` in `AnalysisScreen.kt` by setting `fillMaxWidth()` on its inner `Column` layout, fixing the centering issue for statistical cards (`TOTAL MAKES`, `MAX STREAK`, `MAKE ANGLE`, `MISS ANGLE`).
    - Upgraded `EfficiencyCard` to accept both the `makes` and `totalShots` values, displaying the actual ratio (e.g. "8/10") under the progress percentage circle gauge.
    - Replaced the generic `TrendingUp` icon in `ArcAnalysisCard` with a custom-drawn, dynamic quadratic Bezier curve on a `Canvas`, representing the actual release shot trajectory corresponding to the average angle.
    - Redesigned `ShotSequenceCard` from a simple index box row to a scrollable list of custom cards showing the shot index, color-coded result (MAKE/MISS), and individual shot angles.
    - Verified compilation success with a clean Gradle build.
- **Shot Analysis Metrics Layout & Background Refactoring**:
    - Removed the inconsistent wrapping background box (`onSurfaceVariant` with alpha) from `AnalysisScreen.kt` to allow standard cards to sit directly on the screen's main background, aligning it with other pages.
    - Added the missing `MAKE ANGLE` and `MISS ANGLE` statistics cards inside the SUCCESS state of `AnalysisScreen.kt` for layout parity.
    - Grouped stats cards on `HomeScreen.kt` into a 2-column count/streak row (`Total Shots`, `Streak`) and a 3-column angle row (`Avg Angle`, `Make Angle`, `Miss Angle`) to eliminate odd size differences and spacing.
    - Verified compile safety with a clean Gradle build.
- **Heading Styles Standardisation**:
    - Identified inconsistency where the "Shot Analysis" heading in `AnalysisScreen.kt` was set to `headlineMedium` and lacked font bolding/weight, while other primary page headings (History, Profile, Leaderboard) used `headlineSmall.copy(fontWeight = FontWeight.Black)`.
    - Standardised primary screen headers to `headlineSmall.copy(fontWeight = FontWeight.Black)` across the application.
    - Updated `HomeScreen.kt` to import `FontWeight` and use `headlineSmall.copy(fontWeight = FontWeight.Black)` for its greeting header.
    - Updated `AnalysisScreen.kt` to use `headlineSmall.copy(fontWeight = FontWeight.Black)` for the "Shot Analysis" page header.
    - Updated `SettingsScreen.kt`'s `TopAppBar` title to use `headlineSmall.copy(fontWeight = FontWeight.Black)` for visual alignment.
    - Verified compilation success with a clean Gradle assembleDebug build run.
- **Bottom Navigation Redirection & Active State Fix**:
    - Identified a bug where navigating away from tabs via inline redirects (e.g. History detail screen to Analysis, or Profile "View All" to History) stored stale backstack states that prevented subsequent bottom navbar tab selection.
    - Updated `BottomNavigationBar.kt` to disable `saveState` and `restoreState` during cross-tab navigations to ensure clicking any tab button always opens the root destination of that tab.
    - Introduced a precise `isSameTab` check mapping active screens (`home`, `analysis`, `history`, `profile`, `record`) to prevent redundant navigations.
    - Verified compilation and build stability with a local Gradle run.
- **Analysis Screen FG% Progression Graph Port**:
    - Identified that the `FgProgressionGraph` component was missing from the `AnalysisScreen.kt` SUCCESS view after the recent UI refactors.
    - Updated `AnalysisViewModel.kt` to compute `fgHistory` and `fgHistoryDates` by collecting `statsRepository.getAllSessions()` dynamically upon loading specific, latest, or newly analyzed sessions.
    - Imported and rendered `FgProgressionGraph` inside `AnalysisScreen.kt` between `ShotSequenceCard` and `LastSessionsCard` to maintain UI consistency across the app.
    - Verified clean compilation with no regressions.
- **Session Model usage & Field Goal Ratio Fix**:
    - Identified that `total_shots` was sometimes missing or returned as `0` in historical MongoDB Session documents.
    - Updated `SessionData` to make `totalShots` nullable and default to `0` to prevent deserialization issues.
    - Implemented a robust fallback calculation (`if (totalShots > 0) totalShots else makes + misses`) across `StatsRepositoryImpl`, `CVRepositoryImpl`, `AnalysisViewModel`, `SessionEntity`, and `SessionProvider` to ensure the total shot count is computed correctly in the app logic.
    - Resolved the issue where the app incorrectly rendered ratios like `1/0` for field goals.
    - Verified compilation and unit tests with clean local builds.
- **Adaptive App Icon Rendering Fix**:
    - Restored the adaptive icon support for modern Android (API 26+) by creating `mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`.
    - Defined a solid background color `@color/ic_launcher_background` matching the icon's grey circle color (`#68808C`) and added it to `colors.xml`.
    - Generated centered and scaled transparent `ic_launcher_foreground.png` launcher foreground assets for all densities (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) to ensure the basketball logo elements fit cleanly within the 66% adaptive icon safe zone and do not get clipped.
    - Verified compilation safety with a clean local build using `./gradlew assembleDebug`.
- **Session Schema Alignment and Naming Conventions Update**:
    - Aligned `SessionRequest` and `SessionData` models with the updated MongoDB Session schema conventions.
    - Added `@SerializedName` annotations for snake_case properties (`average_angle`, `average_make_angle`, `average_miss_angle`, `fg_percentage`, `shot_angles`, `shots_results`, `total_shots`) and camelCase properties (`longestStreak`, `sessionDate`, `userId`).
    - Introduced wrapper classes `MongoObjectId` and `MongoDate` to parse MongoDB-specific `$oid` and `$date` JSON sub-objects safely.
    - Updated `StatsRepositoryImpl` to handle optional fields like `fgPercentage` and wrap `userId` in `MongoObjectId` during synchronization.
    - Successfully compiled and ran unit tests to verify zero regressions.
- **Session Data Network Serialization & UI Mapping Fix**:
    - Updated `ArcAnalysisCard` in `AnalysisScreen.kt` to receive and display the actual `averageAngle` property of the shot session.
    - Added dynamic status badges ("PERFECT", "GOOD", "ADJUST ARC") based on the deviation from the user's target angle.
    - Resolved missing session statistics, shot sequences, streaks, and arcs across the Analysis, History, and Leaderboard views.
- **App Icon & Loading Screen Branding Alignment**:
    - Replaced the launcher icons in all density mipmap folders (`mipmap-mdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`) with the user's official circular branded icon (grey circle background with basketball swooshing into net) in PNG format.
    - Cleaned up and deleted the XML-based adaptive icon overrides to prioritize the official circular branded launcher icon.
    - Redesigned `SplashScreen.kt` to include a pulsing brand logo, spaced tagline typography, a custom dual-gradient progress bar, and soft amber background glows.
- **Session Data Architecture Refactoring & Clean Compile**:
    - Fully migrated `ShotAnalysis` references to `Session` across the Room database schemas, the Custom ContentProvider endpoints, and all UI packages (`AnalysisViewModel`, `HistoryViewModel`, `HomeViewModel`, `ProfileViewModel`, `LeaderboardViewModel`, and their respective screens).
    - Resolved JSON serialization conflicts with `longestStreak`.
    - Renamed and refactored the instrumented provider test to `SessionProviderTest.kt`, updating URI paths to `sessions` and successfully verifying clean compilation and local build verification.
- **AI Insights Feature Removal**:
    - Removed all traces of the inaccurate and incomplete AI Training Insights component, UI card, ViewModel state properties, and string resources from the codebase.
    - Added AI Coaching Insights as a designated future enhancement.
- **Custom ContentProvider & Instrumented Tests (Coursework Compliance)**:
    - Added synchronous cursor query and write methods to `SessionDao.kt`.
    - Created `SessionProvider.kt` custom ContentProvider to expose Room database shot sessions via content URIs.
    - Declared and registered the provider inside `AndroidManifest.xml` under the `<application>` element.
- **App Lifecycle & Screen Rotation Fixes**:
    - Refactored transient UI states (zoom/pan parameters `scale`, `offsetX`, `offsetY` in `AnalysisScreen.kt`; `selectionMode` and `isRecording` in `RecordScreen.kt`) to use `rememberSaveable` instead of `remember` so they survive orientation changes.
    - Configured `android:configChanges` for `MainActivity` in `AndroidManifest.xml` to prevent activity destruction and restart on screen rotation.
    - Added `findActivity` extension helper to `AnalysisScreen.kt` and updated `DisposableEffect` to check `isChangingConfigurations` before deleting temporary video files, preventing video data loss on rotation.
    - Verified build stability with `./gradlew assembleDebug`.
- **External Navigation Intent Implementation**: Implemented an external intent button ("Contact Support") in the `SettingsScreen` UI. The button launches `Intent.ACTION_SENDTO` routing to `neaauth@gmail.com` with support email subject prepopulated and a friendly Toast fallback error-handler, resolving the specification requirement for external app navigation.
- **Cleanup of Unused Local Dev Files**: Removed the local python server `backend/` directory and local gradle build log files (`build_output*.txt`) to keep the coursework repository clean and production-focused.
- **Network Timeout Extension**: Extended the base `OkHttpClient` connect, read, and write timeouts in `NetworkModule.kt` to 300 seconds (5 minutes) to ensure large video uploads and heavy CV processing on the production server complete without timeout errors.
- **CV Communication Implementation Plan**: Created a detailed architecture and implementation plan (`cv_communication_plan.md` artifact) addressing network security configurations, custom OkHttp timeouts for large multipart video uploads, orientation-change lifecycle handling, nullable JSON parsing fallbacks, and local integration verification steps.
- **Codebase Comment Audit & Style Refactoring**:
    - Performed a thorough audit of all comments across the codebase to ensure consistency with the user's signature casual, minimal, and all-lowercase style.
    - Removed verbose, repetitive, and AI-like code descriptions.
    - Updated `AuthRepositoryImpl`, `AnalysisScreen`, `AnalysisViewModel`, `LoginScreen`, `RegisterScreen`, `HomeScreen`, `RecordScreen`, `CVRepositoryImpl`, `NetworkConfig`, `ProfileScreen`, and `LeaderboardScreen`.
- **Unused Files & Component Cleanup**:
    - Deleted the unused `ArcAnalysisGraph.kt` component since its layout was replaced by the new progression trend graph.
    - Removed the empty, placeholder `domain/use_case` package.
    - Renamed `LoadingScreen.kt` and its Composable component to `SplashScreen` (`SplashScreen.kt`) to accurately match the destination route and splash package domain, resolving the Fully-Qualified Name (FQN) layout mismatch inside `AppNavigation`.
- **Progression Graph Axes Enhancement (FG% & Date)**:
    - **Y-axis & X-axis Labels**: Redesigned `LineChart` using Compose's `TextMeasurer` and `drawText` to dynamically draw percentage values (0%-100%) on the Y-axis and short session dates (e.g., "19 May") on the X-axis.
    - **UI Layout Adjustments**: Added padding/margins within the chart's canvas to avoid text clipping and relocated the "LATEST 5 SESSIONS" badge to the top-right of `FgProgressionGraph` to prevent label collision.
    - **ViewModel Integration**: Updated both `HomeViewModel` and `ProfileViewModel` to fetch and format the session dates matching `fgHistory` and expose them to the UI states.
    - **Screen Binding**: Linked the new `fgHistoryDates` list in `HomeScreen` and `ProfileScreen` to their respective graphs.
- **Home Screen Graph Refactoring & UI Optimization**:
    - **Shared Chart Component**: Extracted the duplicate `LineChart` from the profile screen to a new shared composable `LineChart.kt` under `ui/components/`.
    - **New Progression Graph**: Introduced `FgProgressionGraph.kt` under `ui/components/` to handle visual headers, margins, and render the shared `LineChart`.
    - **Observed Stats Refactoring**: Updated `HomeViewModel` to fetch and observe `getAllShotAnalyses()` instead of `getLatestShotAnalysis()`, allowing it to compute the latest session stats as well as the progression history over the last 5 sessions (`fgHistory`).
    - **Updated Home Screen UI**: Replaced the obsolete `ArcAnalysisGraph` call on `HomeScreen` with `FgProgressionGraph(fgHistory = uiState.fgHistory)`, creating a clean and aesthetically consistent user experience.
    - **Localization & Build Validation**: Created new string resources `fg_progression` and `performance_trend` in `strings.xml` to decouple graph labels from hardcoded strings. Verified compilation with clean `./gradlew assembleDebug` check.
- **Cache Optimization & Massive Storage Fix**:
    - **Identified Storage Leaks**: Discovered that recorded and gallery-picked video files were being stored in `context.cacheDir` indefinitely.
    - **Manual Cache Control**: Added a "Clear Cache" button to the Support section in Settings, displaying the current cache size (e.g., "Free up 245.50 MB").
    - **Automated Lifecycle Cleanup**: 
        - Modified `AnalysisViewModel` to delete video files immediately after successful upload and processing.
        - Implemented `DisposableEffect` in `AnalysisScreen` to clean up temporary video files if the user exits the selection screen without starting analysis.
    - **Robust Utilities**: Enhanced `FileUtils` with recursive size calculation and deletion logic.
- **Resolved Video Upload Crash**: Fixed a critical crash occurring during the video analysis workflow.
    - **Robust Null Handling**: Identified and fixed a `NullPointerException` risk where server responses with missing `shot_angles` or `shots_results` would crash the app due to non-nullable Kotlin types.
    - **Threading Optimization**: Refactored `CVRepositoryImpl` to strictly use `Dispatchers.IO` for all I/O and network operations, preventing Main thread blocking during large video uploads.
    - **Refined Data Flow**: Eliminated redundant database save operations in `AnalysisViewModel`, consolidating persistence logic within the repository layer.
- **Video Analysis Workflow Refinement**:
    - **Consistent Coordinate Markers**: Fixed an issue where the "L" and "R" hoop selection markers would scale up/down when zooming into the video frame. Implemented an inverse `graphicsLayer` scale in `SelectionMarker` to maintain a constant 24dp size regardless of the zoom level, ensuring precise selection and improved usability.
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

## 🔮 Future Enhancements
- [ ] **AI Coaching Insights**: Re-implement a more accurate and robust AI feedback system with actual telemetry/video classification feedback (removed from MVP due to accuracy and utility issues).


