# RepTrack — Pending Tasks

**Constraint:** No Gradle or dependency changes. Use only current libraries (Compose, Material 3, Room, Hilt, Navigation, Lifecycle, Coroutines, Serialization).

---

## Current State (from codebase)

- **Phases 1–4:** Done (Gradle, data layer, app scaffolding, NavGraph, theme, MainActivity).
- **Phase 5 (Home):** Done — `HomeScreen`, `HomeViewModel`, template cards, navigation to session and to Template List / Editor / Settings.
- **Phase 6 (Active Session):** Done — `ActiveSessionScreen`, `ActiveSessionViewModel`, timer, finish/discard, wired in `NavGraph`.
- **Phase 7 (Exercise Detail):** Done — `ExerciseDetailScreen`, `ExerciseDetailViewModel`, set rows, auto-save, wired in `NavGraph`.
- **Phase 8 (Templates):** Not implemented — screens and ViewModels missing; `NavGraph` has placeholders.
- **Phase 9 (Settings + Export):** Not implemented — screens, ViewModel, and `JsonExporter` missing; `NavGraph` has placeholder.

---

## Pending Tasks

### 1. Phase 5 — Build verification (optional)

- [ ] Run `./gradlew assembleDebug` (or build from Android Studio) and fix any compile/runtime issues so the app runs from Home → Session → Exercise Detail and back.

---

### 2. Phase 8 — Template Manager + Exercise Library

**2.1 Create Template UI layer**

- [ ] **`ui/templates/TemplateViewModels.kt`**
  - Template list VM: load templates (e.g. `repository.getAllTemplates()`), support delete (with confirmation).
  - Template editor VM: load template by id, load template exercises, support name edit, reorder exercises, set default sets per exercise (e.g. stepper 1–10), add/remove exercises (from library), save (insert/update template + template exercises).
  - Exercise library VM: load all exercises (`repository.getAllExercises()`), support create (name, input type, notes), edit, delete.

- [ ] **`ui/templates/TemplateScreens.kt`**
  - **TemplateListScreen:** List of workout templates; tap → navigate to `Screen.TemplateEditor.createRoute(templateId)`; FAB or action to create new template (e.g. new template then navigate to editor); long-press or menu for delete with confirmation.
  - **TemplateEditorScreen:** Name field, ordered list of exercises (each with sets stepper and remove); “Add Exercise” opens picker/dialog from exercise library; save updates via repository (no Gradle/dependency changes).
  - **ExerciseLibraryScreen:** List of exercise definitions; FAB to add new exercise (dialog: name, input type Weight+Reps / Time / Reps only, optional notes); edit/delete existing.

**2.2 Wire navigation**

- [ ] In **`NavGraph.kt`**, replace the three placeholders with:
  - `composable(Screen.TemplateList.route)` → `TemplateListScreen(navController)`
  - `composable(Screen.TemplateEditor.route, ...)` → `TemplateEditorScreen(navController)` (read `templateId` from `SavedStateHandle` in ViewModel).
  - `composable(Screen.ExerciseLibrary.route)` → `ExerciseLibraryScreen(navController)`
- [ ] Use `hiltViewModel()` and `SavedStateHandle` for `templateId` where needed; use existing `Screen` routes and `repository` only.

**2.3 Acceptance (from Build Plan)**

- [ ] Can create a new template, add exercises from the library, set default sets per exercise, reorder, save.
- [ ] Can open Exercise Library, create new exercises (name, input type, notes), edit/delete.

---

### 3. Phase 9 — Settings + JSON Export

**3.1 Export utility**

- [ ] **`util/JsonExporter.kt`**
  - Input: need access to all 5 tables (or repository methods that expose data for export). Prefer adding export methods on `WorkoutRepository` (e.g. `getAllForExport()`) that return data needed for JSON — no new dependencies.
  - Output: single JSON structure (e.g. `exportedAt`, `version`, `exerciseDefinitions`, `workoutTemplates`, `templateExercises`, `workoutSessions`, `sessionSets`) matching the shape in Build Plan.
  - Use existing `kotlinx.serialization` (already in project) for serialization.
  - Write to a file in context-specific cache/files dir, then share via `FileProvider` + `Intent.ACTION_SEND` (authority `${applicationId}.provider`; `res/xml/file_paths.xml` and manifest `<provider>` already exist).

**3.2 Settings UI**

- [ ] **`ui/settings/SettingsViewModel.kt`**
  - Hilt-inject repository and whatever is needed for export (e.g. application context or a small export helper that uses context from caller).
  - Actions: trigger export (call exporter and share), provide app version for display.

- [ ] **`ui/settings/SettingsScreen.kt`**
  - Export button: calls ViewModel export → open share sheet with the generated `.json` file.
  - Disabled “Drive” (or similar) toggle labeled “Coming soon”.
  - Display app version (e.g. from `BuildConfig.VERSION_NAME` or package manager).

**3.3 Wire navigation**

- [ ] In **`NavGraph.kt`**, replace the Settings placeholder with `SettingsScreen(navController)`.

**3.4 Acceptance (from Build Plan)**

- [ ] Tapping Export opens the Android share sheet with a `.json` file containing the exported data in the agreed shape.

---

## Summary

| Phase | Status  | Pending work |
|-------|---------|----------------|
| 5     | Done    | Optional: build verification only. |
| 6–7   | Done    | None. |
| 8     | Pending | Add `TemplateViewModels.kt`, `TemplateScreens.kt`, wire Template List / Editor / Exercise Library in `NavGraph`. |
| 9     | Pending | Add `JsonExporter.kt`, `SettingsViewModel.kt`, `SettingsScreen.kt`, wire Settings in `NavGraph`. |

All of the above can be implemented with the current stack (Compose, Material 3, Room, Hilt, Navigation, Lifecycle, Coroutines, Serialization) and no Gradle or dependency changes.
