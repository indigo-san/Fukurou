package com.example.fukurou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FukurouDrawer(navController: NavHostController, modifier: Modifier = Modifier) {
    ModalDrawerSheet {
        Spacer(Modifier.statusBarsHeight())
        DrawerHeader()
        VerticalDividerItem()
        DrawerItemHeader("Fukurou")

        DrawerItem(
            icon = Icons.Filled.Home,
            text = "ホーム",
            onClick = {
                navController.navigate("home")
            },
            selected = navController.currentDestination?.route == "home"
        )

        DrawerItem(
            icon = Icons.Filled.Assignment,
            text = "レポート",
            onClick = {
                //navController.navigate("home")
            },
            selected = navController.currentDestination?.route == "report"
        )

        DrawerItem(
            icon = Icons.Filled.Assignment,
            text = "教科",
            onClick = {
                navController.navigate("subjects")
            },
            selected = navController.currentDestination?.route == "subjects"
        )

        DrawerItem(
            icon = Icons.Filled.TableRows,
            text = "校時表",
            onClick = {
                navController.navigate("time-frame-settings")
            },
            selected = navController.currentDestination?.route == "time-frame-settings"
        )

        VerticalDividerItem(modifier = Modifier.padding(horizontal = 28.dp))
        DrawerItemHeader("Recent Profiles")
    }
}

@Composable
private fun DrawerHeader() {
//    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
//        JetchatIcon(
//            contentDescription = null,
//            modifier = Modifier.size(24.dp)
//        )
//        Image(
//            painter = painterResource(id = R.drawable.jetchat_logo),
//            contentDescription = null,
//            modifier = Modifier.padding(start = 8.dp)
//        )
//    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DrawerItem(text: String, selected: Boolean, icon: ImageVector, onClick: () -> Unit) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            icon,
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun VerticalDividerItem(modifier: Modifier = Modifier) {
    // TODO (M3): No Divider, replace when available
    androidx.compose.material.Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}
