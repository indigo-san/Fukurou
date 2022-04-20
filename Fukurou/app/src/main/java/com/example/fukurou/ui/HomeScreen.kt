package com.example.fukurou.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fukurou.R
import java.time.LocalDate

private fun getThisWeek(): LocalDate {
    val now = LocalDate.now()
    return now.minusDays(now.dayOfWeek.ordinal.toLong())
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val showUserDialog = remember { mutableStateOf(false) }
    val bottomNavState = rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val weekStart = remember { mutableStateOf(getThisWeek()) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { FukurouDrawer(navController) },
    ) {
        Scaffold(
            floatingActionButton = {
                if (bottomNavState.value == 1) {

                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (bottomNavState.value == 0)
                                    Icons.Filled.Dashboard
                                else
                                    Icons.Outlined.Dashboard,
                                contentDescription = null
                            )
                        },
                        onClick = { bottomNavState.value = 0; },
                        selected = bottomNavState.value == 0,
                        label = { Text(stringResource(id = R.string.dashboard)) }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (bottomNavState.value == 1)
                                    Icons.Filled.Book
                                else
                                    Icons.Outlined.Book,
                                contentDescription = null
                            )
                        },
                        onClick = { bottomNavState.value = 1 },
                        selected = bottomNavState.value == 1,
                        label = { Text(stringResource(id = R.string.lessons)) }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (bottomNavState.value == 2)
                                    Icons.Filled.Description
                                else
                                    Icons.Outlined.Description,
                                contentDescription = null
                            )
                        },
                        onClick = { bottomNavState.value = 2 },
                        selected = bottomNavState.value == 2,
                        label = { Text(stringResource(id = R.string.reports)) }
                    )
                }
            },
            topBar = {
                HomeTopBar(drawerState, showUserDialog, bottomNavState, weekStart)
            }
        ) {
            AnimatedContent(
                modifier = Modifier.padding(it),
                targetState = bottomNavState.value,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } with
                                slideOutHorizontally { width -> -width }
                    } else {
                        slideInHorizontally { width -> -width } with
                                slideOutHorizontally { width -> width }
                    }
                }
            ) { type ->
                when (type) {
                    0 -> {
                        DashboardContent(navController)
                    }
                    1 -> {
                        //LessonsContent(navController, snackbarHostState)
                        TimetableContent(navController, weekStart)
                    }
                    2 -> {
                        ReportsContent(navController, snackbarHostState)
                    }
                    else -> {}
                }
            }
        }
    }

    UserEmailDialog(showUserDialog)
}
