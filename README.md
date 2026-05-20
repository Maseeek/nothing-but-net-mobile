# Nothing But Net – Mobile App Coursework

## 🎯 Project Goal

Create an Android application (Kotlin + Jetpack Compose) that captures basketball shot videos, uploads them to an existing Python backend for analysis, and presents coaching insights to the user.

## 🏗️ Architecture & Design

- **Thin-client model** – the app handles UI, video capture, and data visualisation; all heavy computer-vision processing lives on the server.
- **MVVM** – ViewModel + StateFlow drives the UI, keeping business logic separate from composables.
- **Networking** – Retrofit/Ktor client for REST communication, multipart uploads for video files.
- **Local storage** – DataStore for preferences, Room for caching shot history.
- **Navigation** – Jetpack Navigation Component manages screen flow (Login → Dashboard → Profile → Upload).

## 📁 Repository Layout

```
mobile-app-coursework/
│   README.md                # <-- you are reading it
│
├─ resources/                 # specifications, markdown outlines, PDFs
│   ├─ project_outline.md    # full spec for the app
│   └─ 25COB155_CW_Specification.md
│
├─ app/                       # Main Android application module
│   └─ src/                   # Source code and resources
```

## ✅ How to Test / Verify

1. **Clone the repo** (already in your local workspace).
2. **Open the project in Android Studio**. Choose **File → Open** and select the project root directory (`nothing-but-net-mobile`).
3. **Build the app** – run `./gradlew assembleDebug` (or click _Run_ in Android Studio).
4. **Run unit tests** – execute `./gradlew test` to confirm the test runner works.
5. **Manual UI test** – run the app on an emulator or device and verify navigation works.
6. **Integration test** – use the backend's test endpoint to ensure the video is received and a JSON response is parsed correctly.

## 🛠️ Development Tools

- **Android Studio Flamingo 2024.1.1** (or newer) – IDE with built-in Gradle, emulator, and Compose preview.
- **Kotlin 1.9** – language version used for all source files.
- **Gradle Wrapper** – `gradlew`/`gradlew.bat` ensures consistent build environment.
- **Git** – version control.
- **curl / Postman** – handy for manually testing backend APIs.

---

## 🐍 Backend Integration (Python CV Server)

The heavy lifting for computer vision is done by an external Python Flask server.

### Server Components

The backend server consists of these core files (located in the `backend` folder):

- `requirements.txt`: Python dependencies (Flask, OpenCV, numpy, etc.).
- `src/server.py`: The Flask server defining the API endpoints.
- `src/cv_core.py`: Contains the `BasketballTracker` class implementing the OpenCV ball-tracking logic.

### How to Host the Server

1. Navigate to the `backend` directory.
2. (Optional) Create and activate a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Run the server:
   ```bash
   python src/server.py
   ```
   The server will start on port `5001` (e.g., `http://localhost:5001`).

### API Interaction

The mobile app interacts with the backend primarily through the analysis endpoint.

**Endpoint:** `POST /upload-and-analyze`

**Request Format:** `multipart/form-data`

- `video`: The raw video file captured from the device.
- `hoopLeft`: JSON array `[x, y]` representing the left hoop coordinate.
- `hoopRight`: JSON array `[x, y]` representing the right hoop coordinate.
- `showAngle`: (Optional) Boolean string `'true'` or `'false'`.

**Expected Response:** JSON object containing the analysis

```json
{
  "success": true,
  "data": {
    "total_shots": 10,
    "makes": 7,
    "misses": 3,
    "fg_percentage": 70.0,
    "longest_streak": 4,
    "average_angle": 45.5,
    "average_make_angle": 44.8,
    "average_miss_angle": 47.2,
    "shot_angles": [45.1, 44.5, ...],
    "shots_results": [1, 1, 0, ...]
  }
}
```

**Mobile App Connection:**
In the Android app, use Retrofit to create a multipart request pointing to `http://<server-ip>:5001/upload-and-analyze`. Ensure the app requests internet permissions.

---

## 📦 Useful Commands

| Command                              | Purpose                                                       |
| ------------------------------------ | ------------------------------------------------------------- |
| `./gradlew assembleDebug`            | Build a debug APK.                                            |
| `./gradlew test`                     | Execute unit tests.                                           |
| `./gradlew lint`                     | Run Android lint checks.                                      |
| `git status`                         | View repository changes.                                      |
| `git add . && git commit -m "<msg>"` | Commit work.                                                  |
