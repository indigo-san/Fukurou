package com.example.fukurou.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fukurou.R

@Preview
@ExperimentalMaterial3Api
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavHostController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val fabExpandState = remember { mutableStateOf(false) }
    val showUserDialog = remember { mutableStateOf(false) }
    val bottomNavState = remember { mutableStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { FukurouDrawer(navController) },
    ) {
        Scaffold(
            floatingActionButton = { },
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
                }
            },
        ) {
            when (bottomNavState.value) {
                0 -> {
                    DashboardContent(fabExpandState, drawerState, navController, showUserDialog)
                }
                1 -> {
                    Text("Lessons")
                }
                else -> {}
            }
        }
    }

    UserEmailDialog(showUserDialog)
}
