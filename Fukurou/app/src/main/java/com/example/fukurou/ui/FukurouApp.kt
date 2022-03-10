package com.example.fukurou.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fukurou.ui.details.SchooldayDetailScreen

@ExperimentalMaterial3Api
@Composable
fun FukurouApp() {
    val navController = rememberNavController()
    NavHost(
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

//        composable("create") {
//            CreateMessageScreen(navController = navController)
//        }
    }
}
