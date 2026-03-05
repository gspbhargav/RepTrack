# RepTrack — Android Workout Tracker
## Complete Build Plan (Phase-by-Phase)

---

## Locked Decisions (reference this every time)

| Decision | Value |
|---|---|
| App name | **RepTrack** |
| Package ID | `com.reptrack.app` |
| Language | Kotlin only |
| UI | Jetpack Compose + Material 3 |
| Database | Room (SQLite, fully offline) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 |
| AGP version | **9.0.1** (what's installed) |
| Dumbbell weight | Per dumbbell (label shown in UI) |
| Past sessions | Read-only by default, explicit Edit unlock |
| Export | JSON via Android share sheet |

---

## Starter Exercise Library (pre-seeded on first launch)

### Push Day
- DB Chest Press — Weight+Reps — 3 sets: 5kg×12, 7.5kg×10, 10kg×8
- DB Shoulder Press — Weight+Reps — 3 sets: 5kg×12, 7.5kg×10, 10kg×8

### Pull Day
- Lat Pulldown — Weight+Reps — 3 sets: 20kg×12, 20kg×12, 20kg×12
- Deadlift — Weight+Reps — 3 sets: 15kg×12, 15kg×12, 15kg×12

### Leg Day
- Squats — Reps only (bodyweight) — 2 sets × 12 reps
- Lunges — Weight+Reps (per dumbbell) — 2 sets: 5kg×8, 5kg×8

### Conditioning
- Walking — Time — 1 set × 1200 seconds (20 min)
- Plank — Time — 3 sets × 30 seconds each

---

## Critical Gradle Notes (AGP 9.0.1 specific)

AGP 9.0.1 is very new and has two important quirks vs older versions:

1. **Do NOT add `kotlin-android` plugin** — AGP 9.0.1 bundles Kotlin internally. Adding it separately causes: `Cannot add extension with name 'kotlin', as there is an extension already registered`. Only use `kotlin-compose`, `ksp`, `hilt`, `kotlin-serialization`.

2. **`compileSdk = 35` is fine** with AGP 9.0.1 — no warning.

---

## File Structure to Create

```
app/src/main/kotlin/com/reptrack/app/
  ├── AppModule.kt
  ├── MainActivity.kt
  ├── NavGraph.kt
  ├── RepTrackApp.kt
  ├── data/
  │   ├── db/
  │   │   ├── Daos.kt
  │   │   ├── Entities.kt
  │   │   ├── RepTrackDatabase.kt
  │   │   └── SeedData.kt
  │   └── repository/
  │       └── WorkoutRepository.kt
  ├── domain/
  │   └── model/
  │       └── Models.kt
  ├── ui/
  │   ├── components/
  │   │   └── Components.kt
  │   ├── exercise/
  │   │   ├── ExerciseDetailScreen.kt
  │   │   └── ExerciseDetailViewModel.kt
  │   ├── home/
  │   │   ├── HomeScreen.kt
  │   │   └── HomeViewModel.kt
  │   ├── session/
  │   │   ├── ActiveSessionScreen.kt
  │   │   └── ActiveSessionViewModel.kt
  │   ├── settings/
  │   │   ├── SettingsScreen.kt
  │   │   └── SettingsViewModel.kt
  │   ├── templates/
  │   │   ├── TemplateScreens.kt
  │   │   └── TemplateViewModels.kt
  │   └── theme/
  │       └── Theme.kt
  └── util/
      └── JsonExporter.kt

app/src/main/res/
  ├── xml/
  │   └── file_paths.xml
  └── values/
      ├── strings.xml
      └── themes.xml
```

---

## Phase 1 — Project Setup & Gradle

**Goal:** Clean compilable project with all dependencies ready.

### Step 1A — Create project in Android Studio
- New Project → Empty Activity
- Name: `RepTrack`
- Package: `com.reptrack.app`
- Language: Kotlin
- Min SDK: API 26
- Build config: **Kotlin DSL** ← must select this

### Step 1B — Replace `gradle/libs.versions.toml`

Full contents:
```toml
[versions]
agp = "9.0.1"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.27"
hilt = "2.52"
hiltNavigationCompose = "1.2.0"
room = "2.6.1"
composeBom = "2024.09.00"
navigationCompose = "2.8.3"
lifecycle = "2.8.6"
coroutines = "1.9.0"
serialization = "1.7.3"
coreKtx = "1.13.1"
activityCompose = "1.9.3"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "serialization" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

### Step 1C — Replace root `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

### Step 1D — Replace `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.reptrack.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.reptrack.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
```

### Step 1E — Sync and verify

- File → Sync Project with Gradle Files
- ✅ Sync succeeds with no errors = Phase 1 done
- ❌ If error: paste the error message in chat

---

## Phase 2 — Domain Models + Database Layer

**Goal:** All data classes, Room entities, DAOs, and DB class created and compiling.

**Files to create in this phase:**
- `domain/model/Models.kt`
- `data/db/Entities.kt`
- `data/db/Daos.kt`
- `data/db/RepTrackDatabase.kt`
- `data/db/SeedData.kt`

**Key things to get right:**
- `Models.kt` has pure Kotlin classes — no Android imports
- `Entities.kt` has Room `@Entity` annotations
- `RepTrackDatabase.kt` must list all 5 entities
- `SeedData.kt` contains the baseline weights/reps from the locked decisions above
- ✅ Compiles clean = Phase 2 done

---

## Phase 3 — Repository + Hilt Wiring

**Goal:** Repository layer working, Hilt injecting the DB, app can launch.

**Files to create in this phase:**
- `data/repository/WorkoutRepository.kt`
- `AppModule.kt`
- `RepTrackApp.kt`
- Update `AndroidManifest.xml` — add `android:name=".RepTrackApp"`
- `res/values/strings.xml`
- `res/values/themes.xml`
- `res/xml/file_paths.xml` ← needs `res/xml/` folder created first

**Key things to get right:**
- `WorkoutRepository.kt` contains the clone-last-session logic
- `AppModule.kt` provides all 5 DAOs via `@Provides`
- `RepTrackApp.kt` is annotated with `@HiltAndroidApp`
- `AndroidManifest.xml` must have the FileProvider block for JSON export
- ✅ App launches to a blank screen = Phase 3 done

---

## Phase 4 — Navigation + Theme + MainActivity

**Goal:** Nav graph wired, theme applied, app navigates between screens.

**Files to create in this phase:**
- `NavGraph.kt`
- `ui/theme/Theme.kt`
- Replace `MainActivity.kt`

**Key things to get right:**
- `NavGraph.kt` needs ALL navigation imports explicitly written at top — do not rely on auto-import:
```kotlin
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
```
- `MainActivity.kt` must be annotated with `@AndroidEntryPoint`
- ✅ App launches without crash = Phase 4 done

---

## Phase 5 — Home Screen

**Goal:** Home screen shows 4 workout template cards, tapping one starts a session.

**Files to create in this phase:**
- `ui/components/Components.kt`
- `ui/home/HomeViewModel.kt`
- `ui/home/HomeScreen.kt`

**Key things to get right:**
- `HomeViewModel` calls `repository.seedIfEmpty()` in `init` block
- `HomeViewModel` calls `repository.startNewSession()` when user taps a template
- `HomeScreen` observes `uiState.startingSessionId` — when it becomes non-null, navigate to session screen
- Long-press on template card shows dropdown with Edit / Delete
- ✅ 4 workout cards visible, tapping one navigates to next screen = Phase 5 done

---

## Phase 6 — Active Session Screen

**Goal:** Session screen shows exercises, live timer, finish button.

**Files to create in this phase:**
- `ui/session/ActiveSessionViewModel.kt`
- `ui/session/ActiveSessionScreen.kt`

**Key things to get right:**
- Timer starts from `session.startedAt`, ticks every second via coroutine `delay(1000)`
- Each exercise card shows a summary of sets from the cloned session
- Checkbox per exercise to mark done (grays out the card)
- Finish button calls `repository.finishSession()` then pops back to Home
- Discard shows confirmation dialog, calls `repository.deleteSession()`
- ✅ Can start, check off exercises, finish a session = Phase 6 done

---

## Phase 7 — Exercise Detail Screen

**Goal:** Tapping an exercise opens set rows with weight/reps/time fields, all auto-saving.

**Files to create in this phase:**
- `ui/exercise/ExerciseDetailViewModel.kt`
- `ui/exercise/ExerciseDetailScreen.kt`

**Key things to get right:**
- Shows "Weight is per dumbbell" info row for `WEIGHT_REPS` exercises
- Column headers: SET | WEIGHT (kg) | REPS — or SET | DURATION (sec) for time exercises
- Each row: numbered badge + input fields + delete button
- Fields auto-save on change — no Save button needed
- Add Set button copies values from the last row
- Delete set shows confirmation dialog, then renumbers remaining sets
- ✅ Can edit weights and reps, add/delete sets = Phase 7 done

---

## Phase 8 — Template Manager + Exercise Library

**Goal:** Can create new workout templates and manage the exercise library.

**Files to create in this phase:**
- `ui/templates/TemplateViewModels.kt`
- `ui/templates/TemplateScreens.kt`

**Key things to get right:**
- Template editor has a name field + ordered list of exercises
- Each exercise in template shows a sets stepper (+ / − buttons, min 1 max 10)
- "Add Exercise" opens a picker dialog showing the exercise library
- Exercise library screen has its own FAB to create new exercises
- New exercise dialog has: name field, input type radio buttons (Weight+Reps / Time / Reps only), optional notes
- ✅ Can create a new template and add exercises to it = Phase 8 done

---

## Phase 9 — Settings + JSON Export

**Goal:** Can export all workout data as a JSON file via share sheet.

**Files to create in this phase:**
- `util/JsonExporter.kt`
- `ui/settings/SettingsViewModel.kt`
- `ui/settings/SettingsScreen.kt`

**Key things to get right:**
- `JsonExporter` queries all 5 tables and serializes them to a flat JSON structure
- Uses `FileProvider` + `Intent.ACTION_SEND` to trigger share sheet
- `file_paths.xml` must exist at `res/xml/file_paths.xml`
- `AndroidManifest.xml` must have the `<provider>` block with `${applicationId}.provider`
- Settings screen has Export button, disabled Drive toggle (labeled "Coming soon"), version number
- ✅ Tapping Export opens Android share sheet with a `.json` file = Phase 9 done

---

## JSON Export Shape (for reference)

```json
{
  "exportedAt": "2025-01-15T10:30:00Z",
  "version": 1,
  "exerciseDefinitions": [
    { "id": 1, "name": "DB Chest Press", "inputType": "WEIGHT_REPS", "notes": "Log weight per dumbbell" }
  ],
  "workoutTemplates": [
    { "id": 1, "name": "Push Day", "createdAt": 1736000000000 }
  ],
  "templateExercises": [
    { "id": 1, "templateId": 1, "exerciseDefId": 1, "orderIndex": 0, "defaultSets": 3 }
  ],
  "workoutSessions": [
    { "id": 1, "templateId": 1, "templateName": "Push Day", "startedAt": 1736000000000, "finishedAt": 1736003600000, "notes": "" }
  ],
  "sessionSets": [
    { "id": 1, "sessionId": 1, "exerciseDefId": 1, "exerciseName": "DB Chest Press", "setNumber": 1, "weightKg": 5.0, "reps": 12, "durationSeconds": null, "inputType": "WEIGHT_REPS", "notes": "" }
  ]
}
```

---

## Data Model Summary (5 Room tables)

### exercise_definition
`id · name · inputType (WEIGHT_REPS | TIME | REPS_ONLY) · notes`

### workout_template
`id · name · createdAt`

### template_exercise
`id · templateId · exerciseDefId · orderIndex · defaultSets`

### workout_session
`id · templateId · templateName (snapshot) · startedAt · finishedAt (nullable) · notes`

### session_set
`id · sessionId · exerciseDefId · exerciseName (snapshot) · setNumber · weightKg (nullable) · reps (nullable) · durationSeconds (nullable) · inputType · notes`

> **Snapshots:** `templateName` and `exerciseName` are stored as strings, not foreign key lookups. This means renaming a template later doesn't corrupt old session history.

---

## Clone-Last-Session Logic (most important behaviour)

When user taps a workout template card:

1. Query `workout_session` for the most recent row where `templateId` matches AND `finishedAt IS NOT NULL`
2. Load all `session_set` rows for that session
3. Insert new `workout_session` row (`finishedAt = null`)
4. Clone all sets into the new session (same weights/reps, new `sessionId`)
5. Navigate to Active Session Screen

If no previous session exists → create blank sets using `defaultSets` count per exercise (first-time only).

---

## Common Errors & Fixes

| Error | Cause | Fix |
|---|---|---|
| `Cannot add extension 'kotlin'` | `kotlin-android` plugin conflicts with AGP 9.0.1 | Remove `kotlin-android` from all gradle files — do NOT add it |
| `Unresolved reference 'NavHost'` | Missing imports in NavGraph.kt | Add all 5 navigation imports explicitly at top of file |
| `Unresolved reference 'hiltViewModel'` | Missing Hilt navigation compose dependency | Check `libs.versions.toml` has `hilt-navigation-compose` entry |
| `@Composable invocations can only happen...` | Composable called outside composable context | Usually a missing import causing wrong function to be called |
| `Missing resource @xml/file_paths` | `res/xml/` folder doesn't exist | Create folder: right-click `res/` → New → Android Resource Directory → name: `xml` |
| Gradle sync fails on first run | Dependencies downloading | Wait, retry. Check internet connection. |
| `@HiltAndroidApp` not found | Hilt not compiling | Check `ksp(libs.hilt.compiler)` is in `app/build.gradle.kts` dependencies |

---

## How to Start a New Chat for Each Phase

Paste this at the start of each new chat:

> "I'm building the RepTrack Android workout tracker app. Package is `com.reptrack.app`, Kotlin + Jetpack Compose + Room + Hilt, AGP 9.0.1. **Do NOT add the `kotlin-android` plugin** — it conflicts with AGP 9.0.1. I'm on Phase [X]. [describe what you need]."

This gives the new Claude all the context it needs without repeating everything.
