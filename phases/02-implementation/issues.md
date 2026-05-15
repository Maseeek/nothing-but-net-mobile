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

## [RESOLVED] Null Safety & Threading Crash during Video Send

**Symptoms**:
- App crashes upon clicking "Analyze" or shortly after when the server returns a response.
- Reported by user as "app crashes once i send the video".

**Root Cause**:
- **Null Safety Violation**: `AnalysisViewModel` was not handling potential null values in `shotAngles` and `shotsResults` from the server response, despite the data class declaring them as non-nullable.
- **Main Thread I/O**: Some I/O operations (like reading error bodies or creating request bodies) were potentially happening on the Main thread.
- **Redundant Operations**: Duplicate database save operations were being triggered from both the Repository and ViewModel.

**Solution**:
- Made `shotAngles` and `shotsResults` nullable in `AnalysisResult` model and handled them with Elvis operators (`?: emptyList()`).
- Refactored `CVRepositoryImpl.analyzeVideo` to use `withContext(Dispatchers.IO)` for all operations.
- Removed redundant `saveShotAnalysis` call from `AnalysisViewModel`.
- Added additional null-checks in `AnalysisViewModel` when processing the server response.

## [RESOLVED] HTTP/2 Protocol Error (Stream Reset)

**Symptoms**:
- App shows "Connection Issue" with `stream was reset: PROTOCOL_ERROR` during video upload.
- Specifically happens on physical devices connecting to production servers.

**Root Cause**:
- **HTTP/2 Incompatibility**: A mismatch between OkHttp's HTTP/2 implementation and the cloud load balancer (Render) during large multipart uploads. The server resets the stream when it receives a large body over HTTP/2 without proper flow control handling.

**Solution**:
- Forced `OkHttpClient` to use `Protocol.HTTP_1_1` for all network requests in `NetworkModule.kt`. HTTP/1.1 is more stable for large sequential uploads like videos.

## [RESOLVED] HTTP 502 Bad Gateway (Server Resource Exhaustion)

**Symptoms**:
- App shows "Analysis Failed: error code 502" after a long delay (approx. 50-60s).
- Found in logs that uploaded video files were extremely large (~300MB).

**Root Cause**:
- **Resource Limits**: The Render Free Tier has a 512MB RAM limit. Processing a high-bitrate 4K or 1080p video with Computer Vision models exceeds this limit, causing the backend process to be killed and the proxy to return a 502 Bad Gateway error.
- **Excessive Quality**: `RecordScreen` was set to `Quality.HIGHEST`, which is unnecessary for basketball shot analysis.

**Solution**:
- Reduced camera recording quality to `Quality.SD` (480p) in `RecordScreen.kt`. This significantly reduces file size while maintaining sufficient detail for CV analysis.
- Implemented a proactive **50MB file size limit** in `AnalysisViewModel.kt` to prevent the app from attempting uploads that are likely to fail on the server.
- Improved error messaging to suggest shorter videos if a 502 error still occurs.

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
