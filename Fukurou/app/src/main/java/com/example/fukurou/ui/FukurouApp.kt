package com.example.fukurou.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fukurou.ui.details.LessonDetailScreen
import com.example.fukurou.ui.details.ReportDetailScreen
import com.example.fukurou.ui.details.SchooldayDetailScreen
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDate

@Composable
fun FukurouApp() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    val statusColor = MaterialTheme.colorScheme.background

    SideEffect {
        systemUiController.setSystemBarsColor(statusColor)
    }

    NavHost(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("subjects") {
            SubjectsScreen(navController = navController)
        }

        composable(
            "schoolday-detail/{epochDay}",
            listOf(navArgument("epochDay") { type = NavType.LongType })
        ) { backStackEntry ->
            val epochDay =
                backStackEntry.arguments?.getLong("epochDay") ?: LocalDate.now().toEpochDay()

            SchooldayDetailScreen(
                navController = navController,
                date = LocalDate.ofEpochDay(epochDay)
            )
        }

        composable("create-lesson") {
            LessonDetailScreen(
                navController = navController,
                isCreating = remember { mutableStateOf(true) }
            )
        }

        composable(
            "lesson-detail/{id}", listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            LessonDetailScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("id") ?: -1
            )
        }

        composable(
            "report-detail/{id}", listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            ReportDetailScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("id") ?: -1
            )
        }

        composable("time-frame-settings") {
            TimeFrameScreen(navController = navController);
        }
//        composable("create") {
//            CreateMessageScreen(navController = navController)
//        }
    }
}
