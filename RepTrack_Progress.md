# RepTrack — Build Progress Checkpoint

## Locked Decisions
| Decision | Value |
|---|---|
| App name | RepTrack |
| Package ID | `com.reptrack.app` |
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Database | Room |
| Min SDK | 26 |
| Compile/Target SDK | 36 |
| AGP | 8.9.1 |
| KSP | 2.0.21-1.0.28 |
| Kotlin | 2.0.21 |
| Hilt | 2.56.1 |
| Source folder | `app/src/main/java/com/reptrack/app/` |

---

## Phase Status
- ✅ Phase 1 — Gradle setup
- ✅ Phase 2 — Data layer
- ✅ Phase 3 — App scaffolding
- ✅ Phase 4 — Navigation + Theme + MainActivity
- 🔄 Phase 5 — Home Screen (files created, NavGraph wired, needs build check)
- ⬜ Phase 6 — Active Session Screen
- ⬜ Phase 7 — Exercise Detail Screen
- ⬜ Phase 8 — Template Manager
- ⬜ Phase 9 — Settings + JSON Export

---

## Current `libs.versions.toml`
```toml
[versions]
agp = "8.9.1"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
hilt = "2.56.1"
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

---

## Current `app/build.gradle.kts`
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
    compileSdk = 36

    defaultConfig {
        applicationId = "com.reptrack.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
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
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
```

---

## File Structure Created
```
app/src/main/java/com/reptrack/app/
  ├── AppModule.kt          ✅
  ├── MainActivity.kt       ✅
  ├── NavGraph.kt           ✅
  ├── RepTrackApp.kt        ✅
  ├── data/
  │   ├── db/
  │   │   ├── Daos.kt           ✅
  │   │   ├── Entities.kt       ✅
  │   │   ├── RepTrackDatabase.kt ✅
  │   │   └── SeedData.kt       ✅
  │   └── repository/
  │       └── WorkoutRepository.kt ✅
  ├── domain/
  │   └── model/
  │       └── Models.kt         ✅
  ├── ui/
  │   ├── components/
  │   │   └── Components.kt     ✅
  │   ├── home/
  │   │   ├── HomeScreen.kt     ✅
  │   │   └── HomeViewModel.kt  ✅
  │   └── theme/
  │       └── Theme.kt          ✅
  └── util/                     (empty, created for Phase 9)

app/src/main/res/
  ├── xml/
  │   └── file_paths.xml        ✅
  └── values/
      ├── strings.xml           ✅
      └── themes.xml            ✅
```

---

## Files Still To Create
```
ui/session/
  ├── ActiveSessionScreen.kt    (Phase 6)
  └── ActiveSessionViewModel.kt (Phase 6)
ui/exercise/
  ├── ExerciseDetailScreen.kt   (Phase 7)
  └── ExerciseDetailViewModel.kt(Phase 7)
ui/settings/
  ├── SettingsScreen.kt         (Phase 9)
  └── SettingsViewModel.kt      (Phase 9)
ui/templates/
  ├── TemplateScreens.kt        (Phase 8)
  └── TemplateViewModels.kt     (Phase 8)
util/
  └── JsonExporter.kt           (Phase 9)
```

---

## NavGraph.kt — Current State
All routes defined. HomeScreen wired. Others have placeholder comments to be filled in phases 6-9:
- `// ActiveSessionScreen(navController) — wired in Phase 6`
- `// ExerciseDetailScreen(navController) — wired in Phase 7`
- `// TemplateListScreen(navController) — wired in Phase 8`
- `// TemplateEditorScreen(navController) — wired in Phase 8`
- `// ExerciseLibraryScreen(navController) — wired in Phase 8`
- `// SettingsScreen(navController) — wired in Phase 9`

---

## How to Continue in a New Chat
Paste this at the start:

> "I'm building the RepTrack Android workout tracker. Package `com.reptrack.app`, Kotlin + Jetpack Compose + Material 3 + Room + Hilt. AGP `8.9.1`, Kotlin `2.0.21`, KSP `2.0.21-1.0.28`, Hilt `2.56.1`, compileSdk/targetSdk `36`. Source files are in `app/src/main/java/com/reptrack/app/`. Phases 1-4 are done. Phase 5 (Home Screen) files are created and NavGraph is wired — just needs a build check. Continue from there."
