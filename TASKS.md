# Project Tasks: Nothing But Net

## 🏁 Phase 1: Planning & Design
- [x] Initial Project Setup <!-- id: 0 -->
- [x] Define Core Features and Architecture <!-- id: 1 -->
- [x] Create Project Outline in `resources/` <!-- id: 2 -->
- [x] Finalize UI/UX Wireframes <!-- id: 3 -->
- [x] Define API Contract with Backend <!-- id: 4 -->

## 🛠 Phase 2: Implementation (Mobile MVP)
- [x] Initialize Android Project Structure (`src/`) <!-- id: 5 -->
- [ ] Implement Authentication Flow <!-- id: 6 -->
- [ ] Build Main Dashboard (Compose) <!-- id: 7 -->
    - [ ] Implement Dashboard Header with User Greeting <!-- id: 14 -->
    - [ ] Create Live Session Status Card (Stats + Controls) <!-- id: 15 -->
    - [ ] Implement Quick Stats Grid (Total Shots, Streak, Avg Angle) <!-- id: 16 -->
    - [ ] Design AI Training Insights Component <!-- id: 17 -->
    - [ ] Build Arc Analysis Graph using Canvas/Library <!-- id: 18 -->
    - [ ] Configure Bottom Navigation Bar with custom Floating Action Button <!-- id: 19 -->
- [ ] Integrate Camera and Video Upload <!-- id: 8 -->
- [ ] Add Shot Analysis Visualization <!-- id: 9 -->

## 🧪 Phase 3: Testing & Polish
- [ ] Unit Testing for ViewModels <!-- id: 10 -->
- [ ] UI Testing for Core Flows <!-- id: 11 -->
- [ ] Performance Optimization <!-- id: 12 -->
- [ ] Final Spec Adherence Check <!-- id: 13 -->

---

## 🧠 AGENT MEMORY (Current State)

**Last Updated**: 2026-05-04
**Current Phase**: Phase 2 (Implementation)

### Accomplishments
- Established the ICM (Folders as State) architecture.
- Defined the "Thin Client" architecture for the basketball analysis app.
- Created root `README.md` and initial `TASKS.md`.
- Updated root `README.md` with correct Android Studio import instructions.
- Scaffolded Android Jetpack Compose project with Clean Architecture folders (`data/`, `domain/`, `ui/`) in the `app/` module.
- Added dependencies: Hilt, Room, Retrofit, Navigation Compose, Coroutines.
- Created base `HomeScreen` and `HomeViewModel` to verify setup.
- Analyzed `dashboard-mockup.png` and updated `TASKS.md` with granular UI components.

### Pending
- Implement Authentication Flow (Login UI and Backend Integration).
- Build the full Main Dashboard components (Header, Live Session, Stats, Insights, Arc Analysis).
- Design and implement the custom Bottom Navigation Bar.
