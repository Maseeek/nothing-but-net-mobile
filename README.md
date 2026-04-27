# Nothing But Net – Mobile App Coursework

## 🎯 Project Goal

Create an Android application (Kotlin + Jetpack Compose) that captures basketball shot videos, uploads them to an existing Python backend for analysis, and presents coaching insights to the user.

## 🏗️ Architecture & Design

- **Thin‑client model** – the app handles UI, video capture, and data visualisation; all heavy computer‑vision processing lives on the server.
- **MVVM** – ViewModel + StateFlow drives the UI, keeping business logic separate from composables.
- **Networking** – Retrofit/Ktor client for REST (or GraphQL) communication, multipart uploads for video files.
- **Local storage** – DataStore for preferences, Room for caching shot history.
- **Navigation** – Jetpack Navigation Component manages screen flow (Login → Dashboard → Profile → Upload).

## 📁 Repository Layout (ICM – Folders as State)

```
mobile-app-coursework/
│   README.md                # <‑‑ you are reading it
│   TASKS.md                 # project‑wide task tracker
│
├─ .agents/                  # agent configuration & skills
│   ├─ rules/                # static rule files
│   └─ skills/               # reusable skill scripts (state‑update, task‑manager)
│
├─ phases/                    # state folders for each development phase
│   ├─ 01‑planning/          # active phase currently (README.md with objectives)
│   └─ 02‑implementation/   # next phase (TODO list, scaffold)
│
├─ resources/                 # specifications, markdown outlines, PDFs
│   ├─ project_outline.md    # full spec for the app (see below)
│   └─ 25COB155_CW_Specification.md
│
└─ src/                       # **will hold the Android source tree** once generated
    └─ (empty presently)
```

## ✅ How to Test / Verify

1. **Clone the repo** (already in your local workspace).
2. **Open the project in Android Studio** (or VS Code with the _Kotlin_ plugin). Choose **File → New → Import Project** and point at the `src/` folder once it exists.
3. **Build the app** – run `./gradlew assembleDebug` (or click _Run_ in Android Studio). The build should succeed with no source files yet; this validates the Gradle wrapper.
4. **Run unit tests** – placeholder tests will appear in `src/test/`. Execute `./gradlew test` to confirm the test runner works.
5. **Manual UI test** – after the first implementation step (login screen), run the app on an emulator or device and verify navigation works.
6. **Integration test** – once the upload feature is ready, use the backend's test endpoint (`/api/preview`) to ensure the video is received and a JSON response is parsed correctly.

> **NOTE**: At this stage the app is scaffolding‑only; the real functional tests will be added in Phase 2.

## 🛠️ Development Tools

- **Android Studio Flamingo 2024.1.1** (or newer) – IDE with built‑in Gradle, emulator, and Compose preview.
- **Kotlin 1.9** – language version used for all source files.
- **Gradle Wrapper** – `gradlew`/`gradlew.bat` ensures consistent build environment.
- **Git** – version control (commits are automatically tracked by the Antigravity agent).
- **curl / Postman** – handy for manually testing backend APIs.
- **Agent Skills** –
  - `state-update` – records progress in the current phase’s README.
  - `task-manager` – keeps `TASKS.md` in sync with completed work.

## 📋 How to Use the Agent‑Driven Workflow

1. **Read the active phase** – open `phases/01-planning/README.md` to see current objectives.
2. **Update state** – after finishing a step, run the _state‑update_ skill (the agent does this automatically) which appends a short summary to the phase README.
3. **Track tasks** – the _task‑manager_ skill updates `TASKS.md` checkboxes based on completed work.
4. **Advance phases** – once all items in a phase are checked, the agent will prompt to move to the next phase folder.

## 🚀 Next Steps (Phase 2 – Implementation)

- Initialise the Android project skeleton inside `src/` (`android init` via the Antigravity skill or manual `gradle init`).
- Implement the authentication flow and basic dashboard UI.
- Add camera permission handling and video capture composable.
- Wire up Retrofit service for video upload.

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

_Generated by the Antigravity agent following the ICM workflow._

---

## 🔧 Development Workflow with Antigravity

The Antigravity agent follows the **Folders‑as‑State (ICM)** methodology:

1. **Read the active phase** – open `phases/<phase‑name>/README.md` to see what needs to be done.
2. **Implement code** – make changes in `src/` (or create new files) as instructed.
3. **State Update** – after a logical chunk is finished, the agent automatically runs the **state‑update** skill to append a short summary to the phase’s `README.md`.
4. **Task Management** – the **task‑manager** skill keeps `TASKS.md` in sync, checking off completed items.
5. **Advance Phase** – once all check‑boxes in a phase are ticked, the agent prompts you to move to the next phase folder.

> This loop ensures every change is documented and traceable, mirroring a lightweight project‑management system.

## 📦 Useful Commands

| Command                              | Purpose                                                       |
| ------------------------------------ | ------------------------------------------------------------- |
| `./gradlew assembleDebug`            | Build a debug APK (or run from Android Studio).               |
| `./gradlew test`                     | Execute unit tests.                                           |
| `./gradlew lint`                     | Run Android lint checks.                                      |
| `git status`                         | View repository changes.                                      |
| `git add . && git commit -m "<msg>"` | Commit work – the agent will note this in the state document. |

## 🧭 Understanding the ICM State

- **phases/** – each sub‑folder represents a project stage. The `README.md` inside documents objectives, progress, and a checklist.
- **TASKS.md** – a global task board that mirrors the check‑boxes across phases. The agent updates this file automatically.
- **.agents/** – contains the agent’s configuration, rules, and reusable _skills_ (`state-update`, `task-manager`). Do not edit these unless you are extending the agent.

## 🤝 Contributing

If you are a teammate or a reviewer:

- Fork the repository (or clone the workspace) and create a new branch for your work.
- Follow the same ICM steps: update the relevant phase README, run the state‑update skill, and let the task‑manager sync `TASKS.md`.
- Submit a Pull Request; the agent will automatically add a summary of the PR to the phase notes.

---

_For any questions about the workflow, refer to the `.agents/rules/` directory or ask the Antigravity agent._
