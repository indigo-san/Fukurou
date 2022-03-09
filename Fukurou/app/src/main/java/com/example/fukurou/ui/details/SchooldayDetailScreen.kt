package com.example.fukurou.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Schoolday
import com.example.fukurou.ui.DateFormat

@Preview(showBackground = true)
@Composable
fun PreviewBody() {
    val scd = DemoDataProvider.getNextSchoolday()
    if (scd != null) {
        SchooldayDetailBody(scd)
    }
}

@Composable
fun SchooldayDetailBody(item: Schoolday) {
    Column {
        DateFormat(item.date, modifier = Modifier.padding(16.dp))
        val selectedIndex = remember { mutableStateOf(0) }

        NavigationBar(modifier = Modifier.height(64.dp)) {
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Filled.Book,
                        contentDescription = stringResource(id = R.string.lessons)
                    )
                },
                selected = selectedIndex.value == 0,
                onClick = { selectedIndex.value = 0 }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Filled.Assignment,
                        contentDescription = stringResource(id = R.string.reports)
                    )
                },
                selected = selectedIndex.value == 1,
                onClick = { selectedIndex.value = 1 }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SchooldayDetailScreen(navController: NavHostController, id: Int) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back)
                        )
                    }

                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = stringResource(id = R.string.cd_back)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)

                    }
                }
            )
        },
        content = { SchooldayDetailBody(DemoDataProvider.getSchoolday(id)) }
    )
}