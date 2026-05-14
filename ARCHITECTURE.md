# NOTHING BUT NET - ARCHITECTURE MAP

This document provides a high-level overview of the application's architecture to maintain global context during development.

## 📱 Navigation Routes (Jetpack Compose)
Defined in `AppNavigation.kt`:

- `splash`: Initial loading screen.
- `login`: User authentication.
- `register`: New user creation.
- `home`: Main dashboard with stats and greeting.
- `record`: Camera/Gallery video selection/capture.
- `analysis?videoUri={videoUri}`: Hoop selection and CV processing.
- `history`: List of all past sessions.
- `profile`: User stats, charts, and logout.

## 🏗️ Core Architecture (MVVM + Clean Architecture)

- **UI Layer**: Jetpack Compose Screens (`ui/screens/`) and ViewModels.
- **Domain Layer**: Repositories Interfaces (`domain/repository/`).
- **Data Layer**: Room Entities/DAOs (`data/local/`), Retrofit APIs (`data/remote/`), and Repository Implementations.

## 💾 Local Data Models (Room)
Primary entities:

- `ShotAnalysisEntity`: Stores basketball session results (shots made, total shots, average arc angle, timestamps).

## 🔌 Networking (Retrofit)

- **AuthApi**: Handles login/register, session management, and stats (`AUTH_BASE_URL`).
    - `POST /api/session`: Save a new session.
    - `GET /api/sessions/{userId}`: Fetch all sessions.
    - `GET /api/field-goal-percentage/{userId}`: Fetch career FG%.
    - `GET /api/longest-streak/{userId}`: Fetch best streak.
- **CVApi**: Handles video upload and computer vision analysis (`CV_BASE_URL`).
    - `POST /upload-and-analyze`: Process video for shot detection.
- **Qualifiers**: `@AuthRetrofit` and `@CVRetrofit` distinguish between these services.

## 💉 Dependency Injection (Hilt)
Key modules in `di/`:

- `DatabaseModule`: Provides Room database and DAOs.
- `NetworkModule`: Provides Retrofit instances and API services.
- `RepositoryModule`: Binds Repository interfaces to implementations.

## 🛠️ Key Utilities

- `TokenManager`: Handles JWT storage and login state (stores `userId`, `username`, `token`).
- `FileUtils`: Bridges Android Content URIs to standard File processing.
- `NetworkConfig`: Toggle between local and production environment URLs.

