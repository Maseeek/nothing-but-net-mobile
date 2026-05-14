# PHASE 2 - ISSUE LOG

Persistent tracking of complex bugs, crashes, and technical roadblocks.

---

## [RESOLVED] Video Upload Crash (Signal 9 / Process Terminated)

**Symptoms**:
- App crashes/terminates immediately upon starting video upload in `AnalysisScreen`.
- Error logs showed `Signal 9` or `Process terminated` without a standard Java stack trace.

**Root Cause**:
- **Main Thread ANR**: Large video file processing (URI to File conversion) was happening on the Main thread, causing the OS to kill the process.
- **Resource Exhaustion**: Heavy memory/CPU usage during upload without proper Coroutine context switching.

**Solution**:
- Moved video file processing and API calls to `Dispatchers.IO`.
- Added defensive `NaN` checks for hoop coordinate selection.
- Hardened `CVRepositoryImpl` with null-safety defaults for backend responses.

---

## [OPEN] Known Constraints

- **Emulator Testing**: CameraX functionality is limited on standard emulators; requires physical device for full validation of `RecordScreen`.
- **Large Video Files**: Backend currently has a timeout/size limit (approx 50MB) which needs to be handled gracefully in the UI.

---

## [RESOLVED] Build Failure (Unresolved References)

**Symptoms**:
- Gradle build fails with `Unresolved reference 'CircleShape'` and `Unresolved reference 'clickable'` in `HistoryScreen.kt`.

**Root Cause**:
- Missing imports for `androidx.compose.foundation.shape.CircleShape` and `androidx.compose.foundation.clickable` after refactoring.

**Solution**:
- Added missing imports to `HistoryScreen.kt`.
- Verified build stability with `assembleDebug`.
