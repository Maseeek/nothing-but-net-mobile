# 25COB155 Coursework Spec Adherence & Code Quality Report

This report provides a critical analysis of the **Nothing But Net** mobile application codebase against the **25COB155 Mobile Application Development Coursework Specification**. It outlines completed requirements, missing components, security gaps, and minor technical flaws to assist with project planning.

---

## 📊 Specification Adherence Dashboard

| Requirement Category | Specific Requirement | Status | File / Component Reference | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Mandatory Tech** | Native Android App in Kotlin | **Fully Covered** | Entire codebase | Developed native Kotlin app with modern Jetpack Compose UI. |
| **Mandatory Tech** | Minimum of two distinct screens | **Fully Covered** | `ui/screens/` | Contains 9 distinct screens (Home, Analysis, Profile, Settings, etc.). |
| **Mandatory Tech** | Navigation Component | **Fully Covered** | [AppNavigation.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/ui/navigation/AppNavigation.kt) | Implements Jetpack Compose Navigation (`NavHost`). |
| **Mandatory Tech** | Intent for external navigation | **Fully Covered** | [SettingsScreen.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/ui/screens/settings/SettingsScreen.kt) | Implements mail client Intent for user support. |
| **Mandatory Tech** | Handle App Lifecycle (Rotation) | ⚠️ **Partially Covered** | `AnalysisScreen.kt`, `RecordScreen.kt` | Implemented, but configuration changes cause UI state and camera reset bugs. |
| **Mandatory Tech** | Custom `ContentProvider` + Tests | 🔴 **Missing** | N/A | No `ContentProvider` is declared in manifest or implemented in source. |
| **Mandatory Tech** | Local storage & permissions | **Fully Covered** | Room DB, `RecordScreen.kt` | Uses Room database and manages camera/audio permissions responsibly. |
| **Optional Features** | Select exactly 3 features | ⚠️ **Partially Covered** | `AnalysisScreen.kt`, `CVRepositoryImpl.kt` | Only **two** features implemented (Touch Gestures, AI Integration). A 3rd is needed. |
| **Quality & Usability** | Code style, readability, complexity | **Fully Covered** | Entire codebase | Follows high-quality idiomatic Kotlin and Clean Architecture. |
| **Quality & Usability** | Automated Test Coverage | 🔴 **Missing** | `test/`, `androidTest/` | Only boilerplate classes exist; no actual ViewModel or UI tests are written. |

---

## 🔍 Detailed Analysis of Mandatory Requirements

### 1. External App Navigation (Intents) — **COVERED**
* **Specification Requirement:** "Use Intent for external app navigation."
* **Current State:** The `SettingsScreen.kt` launches an external mail client using an intent (`Intent.ACTION_SENDTO` with a `mailto:` URI) to contact support.
* **Critique:** This requirement is satisfied. However, it can be supplemented with an **Android ShareSheet** to satisfy the third optional module feature.


### 2. Custom ContentProvider & Instrumented Tests — **MISSING**
* **Specification Requirement:** "Create a custom ContentProvider with instrumented tests."
* **Current State:** There is no `<provider>` element in `AndroidManifest.xml`, and no class inherits from `ContentProvider`. The instrumented test package only contains a template class (`ExampleInstrumentedTest.kt`).
* **Critique:** This is a major omission. A custom ContentProvider is explicitly required (25% weight category).
* **Remediation Action:** 
  - Create a custom `ContentProvider` class (e.g., `ShotAnalysisProvider`) that exposes the local Room database `ShotAnalysisEntity` table to other applications.
  - Register it in `AndroidManifest.xml`.
  - Write dedicated instrumented tests inside `androidTest/` to query, insert, and delete data using the provider interface.

### 3. App Lifecycle and Screen Rotation — **CRITICAL BUGS IDENTIFIED**
* **Specification Requirement:** "Handle App Lifecycle correctly (e.g., screen rotation)."
* **Current State:** The orientation is not locked in `AndroidManifest.xml` (which is good), and `AnalysisScreen` checks `isLandscape` to adapt layouts. However, several critical local state variables use non-persistent memory wrappers.
* **Critique:** 
  - **Zoom & Pan Reset:** In `AnalysisScreen.kt`, the zoom/pan parameters (`scale`, `offsetX`, `offsetY`) are defined using `remember { mutableFloatStateOf(...) }`. When the screen rotates, the Compose layout recomposes from scratch, resetting these values to default. This disrupts coordinate selection.
  - **Session State Loss:** In `RecordScreen.kt`, `selectionMode` is defined as `remember { mutableStateOf(SelectionMode.NONE) }`. If the user is in `SelectionMode.CAMERA` (recording preview) and rotates the screen, the state resets to `NONE`, kicking the user out of the camera.
  - **Recording UI Reset:** In `RecordScreen.kt`, `isRecording` is defined with `remember { mutableStateOf(false) }`. Rotating the screen during recording resets this to `false` visually, causing the UI state to mismatch the actual active CameraX recorder state.
* **Remediation Action:** Refactor transient UI state using `rememberSaveable` instead of `remember` so that values survive orientation changes.

---

## ⚡ Optional Features Selection

The specification requires selecting **exactly three** optional features from the list. The current project has only selected **two**:

1. **Touch Gesture Capturing (Covered):** Implemented in `AnalysisScreen.kt` for zooming and panning the thumbnail view (`detectTransformGestures`) and placing coordinates via tap (`detectTapGestures`).
2. **AI Feature Integration (Covered):** Implemented via the multipart video upload to a remote Computer Vision server that detects shooting arcs, makes/misses, and calculates trajectory details.
3. **Third Feature (MISSING):**
   - *Firebase:* No dependencies or setup.
   - *Broadcast event handling:* No receivers implemented.
   - *Android ShareSheet:* No share intent exists.
   - *Notifications:* Settings screen has a UI toggle for notifications, but no actual notifications are created or sent.
   - *Adaptive layouts:* Only rotation adaptation, no screen-size adaptive class handling (e.g., tablet layouts).

### Remediation Plan:
Choose one of the following to satisfy the third requirement:
- **ShareSheet (Recommended - Easiest):** Add a "Share Stats" button to the `AnalysisScreen` or `HistoryScreen` that triggers an Android ShareSheet using a send intent (`Intent.ACTION_SEND` and `Intent.createChooser`) containing a text summary of the session stats. This would also satisfy the "Intent for external navigation" requirement, resolving two gaps simultaneously.
- **Notifications:** Send a local status bar notification when the Computer Vision video processing starts and finishes.

---

## 🔒 Security & Best Practices Critique

### 1. Plaintext Sensitive Data Storage
* **File:** [TokenManager.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/data/local/TokenManager.kt)
* **Mistake:** Raw auth tokens and user IDs are saved in default `SharedPreferences` in plain text.
* **Risk:** Rooted devices or debugger backups can easily extract authentication tokens.
* **Remediation:** Migrating to `EncryptedSharedPreferences` (part of `androidx.security:security-crypto` library) secures the storage of session credentials.

### 2. Cleartext Traffic Failure for Local Testing
* **File:** [NetworkConfig.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/data/remote/NetworkConfig.kt)
* **Mistake:** Local URLs (`http://10.0.2.2:3000/` and `http://10.0.2.2:5001/`) use cleartext HTTP traffic.
* **Critique:** The application lacks a network security configuration. Starting with Android 9 (API 28), cleartext traffic is disabled by default. If `IS_PRODUCTION` is toggled to `false`, network requests will fail with cleartext traffic block exceptions.
* **Remediation:** Create a `network_security_config.xml` file that permits cleartext communication explicitly for `10.0.2.2` during development and link it in the manifest.

### 3. Lack of Database Transactions
* **File:** [StatsRepositoryImpl.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/data/repository/StatsRepositoryImpl.kt)
* **Mistake:** The `syncWithServer()` function deletes all cached analyses (`shotAnalysisDao.deleteAll()`) and then iterates to insert new ones (`shotAnalysisDao.insertAnalysis(...)`) without wrapping the operation in a database transaction.
* **Risk:** If the app is interrupted or closed mid-operation, the database is left in a corrupted, empty, or partially-filled state.
* **Remediation:** Annotate the database replacement sequence or Dao methods with Room's `@Transaction` to guarantee database consistency.

### 4. Flow Collection Memory Leak & State Clashing
* **File:** [AnalysisViewModel.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/ui/screens/analysis/AnalysisViewModel.kt)
* **Mistake:** Inside the suspend functions `loadLatestAnalysis` and `loadSpecificAnalysis`, the ViewModel calls `statsRepository.getAllShotAnalyses().collect { ... }`.
* **Critique:** Calling `.collect` inside launched coroutines creates an active collector stream that stays active indefinitely (as Room flows do not terminate). Each call to these methods leaks a new database listener stream. If a user navigates between multiple analyses, multiple collectors will actively compete to overwrite `_uiState`.
* **Remediation:** Use `.first()` or `.firstOrNull()` to retrieve single snapshots in one-shot methods, or handle Flow collection in a standard declarative way (e.g., standard state Flow combining) instead of manual collectors.

### 5. Offline Access Blocked on Analysis Screen
* **File:** [AnalysisViewModel.kt](file:///c:/Users/Maseeek/.gemini/antigravity/scratch/mobile-app-coursework/app/src/main/java/com/example/nothingbutnetmobile/ui/screens/analysis/AnalysisViewModel.kt)
* **Mistake:** The `loadLatestAnalysis` function enforces a remote server synchronization prior to querying the database:
  ```kotlin
  val syncResult = statsRepository.syncWithServer()
  if (syncResult.isFailure) {
      _uiState.value = _uiState.value.copy(
          status = AnalysisStatus.ERROR,
          errorMessage = "Connection Error..."
      )
      return@launch
  }
  ```
* **Critique:** If the user is offline, the sync fails, the UI throws an error screen, and the method exits. The app completely fails to load the local Room database cache! This contradicts the purpose of local database caching.
* **Remediation:** Attempt sync in the background, but load local data first. If the sync fails, simply show a temporary warning/retry indicator rather than preventing the display of cached data.

---

## 🛠 Action Plan for Next Development Cycle

To achieve 100% specification compliance and eliminate critical quality/security errors, execute these steps:

1. **Implement ShareSheet (Satisfies 2 Requirements):**
   - Add a share button to the stats UI.
   - Create an external `Intent` with `Intent.ACTION_SEND` to share session details. This covers **Intent Navigation** (mandatory) and **ShareSheet** (the 3rd optional feature).
2. **Implement Custom ContentProvider:**
   - Write a lightweight `ContentProvider` mapping the `ShotAnalysisEntity` table.
   - Write concrete instrumented tests in `androidTest` that perform queries/inserts on the provider.
3. **Fix Screen Rotation Lifecycles:**
   - Replace `remember` with `rememberSaveable` in `RecordScreen.kt` for `selectionMode` and `isRecording`.
   - Replace `remember` with `rememberSaveable` in `AnalysisScreen.kt` for `scale`, `offsetX`, and `offsetY`.
4. **Fix ViewModel flow leak & Offline Block:**
   - In `AnalysisViewModel.kt`, replace `collect` in one-off methods with `.first()`.
   - Modify `loadLatestAnalysis` to fetch cached database results *before* attempting network sync, ensuring offline operation.
5. **Implement Basic Unit & UI Tests:**
   - Write standard Unit tests for ViewModels (e.g., testing `calculateLongestStreak`).
   - Write simple UI tests (e.g., verifying dashboard elements load).
