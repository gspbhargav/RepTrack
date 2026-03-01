package com.reptrack.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.reptrack.app.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ActiveSession : Screen("active_session/{sessionId}") {
        fun createRoute(sessionId: Long) = "active_session/$sessionId"
    }
    object ExerciseDetail : Screen("exercise_detail/{sessionId}/{exerciseDefId}") {
        fun createRoute(sessionId: Long, exerciseDefId: Long) = "exercise_detail/$sessionId/$exerciseDefId"
    }
    object TemplateList : Screen("template_list")
    object TemplateEditor : Screen("template_editor/{templateId}") {
        fun createRoute(templateId: Long) = "template_editor/$templateId"
    }
    object ExerciseLibrary : Screen("exercise_library")
    object Settings : Screen("settings")
}

@Composable
fun RepTrackNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.ActiveSession.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) {
            // ActiveSessionScreen(navController) — wired in Phase 6
        }
        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.LongType },
                navArgument("exerciseDefId") { type = NavType.LongType }
            )
        ) {
            // ExerciseDetailScreen(navController) — wired in Phase 7
        }
        composable(Screen.TemplateList.route) {
            // TemplateListScreen(navController) — wired in Phase 8
        }
        composable(
            route = Screen.TemplateEditor.route,
            arguments = listOf(navArgument("templateId") { type = NavType.LongType })
        ) {
            // TemplateEditorScreen(navController) — wired in Phase 8
        }
        composable(Screen.ExerciseLibrary.route) {
            // ExerciseLibraryScreen(navController) — wired in Phase 8
        }
        composable(Screen.Settings.route) {
            // SettingsScreen(navController) — wired in Phase 9
        }
    }
}