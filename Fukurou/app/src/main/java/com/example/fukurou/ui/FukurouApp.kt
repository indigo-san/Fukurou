package com.example.fukurou.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
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

        composable(
            "schoolday-detail/{id}", listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            SchooldayDetailScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("id") ?: -1
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

//        composable("create") {
//            CreateMessageScreen(navController = navController)
//        }
    }
}
