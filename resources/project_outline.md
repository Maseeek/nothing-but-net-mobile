# **Project Outline: Nothing But Net (Mobile App)**

## **1. Overview**

**Goal:** Develop a mobile application for basketball shot analysis that connects to an existing Python backend.

**Concept:**

The mobile app acts as a **thin client**, handling video capture, user interaction, and visualization, while all heavy processing (computer vision + analysis) is handled by the server.

**Core Features:**

- Record or upload basketball shots
- Send video data to backend for processing
- Receive structured feedback (JSON)
- Display analytics, trends, and coaching insights

---

## **2. Architecture**

### **2.1. Mobile-Server Model**

- **Mobile App (Android):**
    - UI/UX
    - Video capture & upload
    - Display analytics
    - Handle user interaction
- **Backend (Existing Python Server):**
    - Authentication
    - Video processing (OpenCV / MediaPipe / ML models)
    - Shot detection & classification
    - Feedback generation
    - Data storage

### **2.2. Communication**

- REST API (or GraphQL)
- JSON responses
- Multipart upload for videos

---

## **3. Tech Stack**

### **Mobile App**

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM (ViewModel + StateFlow)
- **Networking:** Retrofit / Ktor Client
- **Navigation:** Jetpack Navigation Component
- **Storage:** DataStore / Room (for caching & preferences)

### **Backend (Reuse Existing)**

- Python (Flask / FastAPI / Django)
- OpenCV / MediaPipe / TensorFlow
- PostgreSQL / MongoDB

---

## **4. Core Features (Mobile)**

### **4.1. User System**

- Sign up / Login (API-based)
- Persistent sessions (token storage)
- Profile screen:
    - Shot history
    - Stats overview

---

### **4.2. Video Capture & Upload**

- Record shot using camera
- Upload video to backend
- Progress indicator during upload
- Handle retries / failures

---

### **4.3. Shot Analysis (Server-Driven) & Profile handling**

- Send video → backend processes:
    - Shooting statistics (shot angle, fg%)
    - Ball trajectory tracking
    - Shot classification (make/miss)
- Receive structured JSON:

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
    "shot_angles": [45.1, 44.5, 47.2, 45.5, 44.2, 46.8, 45.0, 44.9, 47.5, 45.3],
    "shots_results": [1, 1, 0, 1, 1, 0, 1, 1, 0, 1]
  }
}
```

### 4.3.1 Profile display

```json
{
  "userId": "65f1234567890abcdef12345",
  "username": "maseeek",
  "email": "user@example.com",
  "emailVerified": true,
  "isPro": false
}
```

### Session Data

```json
[
  {
    "_id": "65f1234567890abcdef12345",
    "sessionDate": "2024-03-15T14:30:00.000Z",
    "makes": 12,
    "misses": 8,
    "longest_streak": 5,
    "fg_percentage": 60.0
  },
  {
    "_id": "65f1234567890abcdef12346",
    "sessionDate": "2024-03-14T10:15:00.000Z",
    "makes": 8,
    "misses": 12,
    "longest_streak": 3,
    "fg_percentage": 40.0
  }
]
```

---

### **4.4. Analytics & Feedback UI**

- Shot result display
- Visual feedback (charts, overlays)
- Heatmaps (shooting zones)
- Trends over time

---

### **4.5. Dashboard**

- Summary stats:
    - FG%
    - Shot distribution
- Graphs:
    - Performance trends
    - Improvement over time

---

### **4.6. Video Playback**

- Replay uploaded shots
- Overlay feedback (angles, trajectory)
- Side-by-side comparison (future feature)

---

## **5. Functionality → Technical Mapping**

| **Functionality** | **Technical Requirement** |
| --- | --- |
| Multiple screens (Dashboard, Upload, Profile) | Jetpack Compose + Navigation |
| Tap to view shot details | Navigation Component |
| Upload video | Retrofit + Multipart API |
| Shot processing | Backend (Python CV models) |
| Wishlist / saved shots | Local storage (Room/DataStore) |
| Open external links (share, etc.) | Android Intents |
| Notifications (processing complete) | Firebase Cloud Messaging |
| State management | ViewModel + StateFlow |
| Permissions (Camera, Storage) | Android Permissions API |
| Save preferences | DataStore |

---

## **6. Implementation Plan**

### **Phase 1: Mobile MVP**

- Set up Android project (Compose + MVVM)
- Connect to existing backend APIs
- Implement authentication flow
- Basic dashboard UI

---

### **Phase 2: Video Upload**

- Integrate camera recording
- Implement video upload endpoint
- Handle loading + error states

---

### **Phase 3: Data Visualization**

- Display shot results from API
- Build charts and stats UI
- Show feedback and insights

---

### **Phase 4: UX & Optimization**

- Improve performance and responsiveness
- Add caching for offline viewing
- Optimize API calls

---

### **Phase 5: Notifications & Polish**

- Notify users when analysis is complete
- Improve animations and transitions
- Final UI polish

---

## **7. Future Enhancements**

- **Real-Time Analysis:** Stream frames instead of full video upload
- **Offline Mode:** Cache results locally
- **AI Coaching:** Personalized training plans
- **Social Features:** Share stats, compete with friends
- **Wearable Integration:** Track movement data
- On device processing, eliminate upload time

---

## **8. Key Design Decision**

**Thin Client Architecture**

The mobile app:

- Does **not** perform heavy computation
- Acts as a **viewer + uploader**
- Relies entirely on backend for:
    - Computer vision
    - Shot feedback
    - Data processing

**Benefits:**

- Faster mobile development
- Easier to update ML models
- Lower device requirements
- Centralized logic
