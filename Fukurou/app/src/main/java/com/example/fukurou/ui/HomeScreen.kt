package com.example.fukurou.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fukurou.R

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview
@ExperimentalMaterial3Api
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavHostController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val showUserDialog = remember { mutableStateOf(false) }
    val bottomNavState = rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { FukurouDrawer(navController) },
    ) {
        Scaffold(
            floatingActionButton = { },
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
                SearchLayout(drawerState, showUserDialog)
            }
        ) {
            AnimatedContent(
                targetState = bottomNavState.value,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } with
                                slideOutHorizontally { width -> -width }
                    } else {
                        slideInHorizontally { width -> -width } with
                                slideOutHorizontally { width -> width }
                    }
//                        .using(
//                        SizeTransform(clip = false)
//                    )
                }
            ) { type ->

                when (type) {
                    0 -> {
                        DashboardContent(navController)
                    }
                    1 -> {
                        LessonsContent(navController, snackbarHostState)
                    }
                    2 -> {
                        Text("Reports")
                    }
                    else -> {}
                }
            }
        }
    }

    UserEmailDialog(showUserDialog)
}
