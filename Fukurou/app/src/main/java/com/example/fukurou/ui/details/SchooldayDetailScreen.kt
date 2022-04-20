package com.example.fukurou.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Schoolday
import com.example.fukurou.timeformatter
import com.example.fukurou.ui.DateFormat
import com.example.fukurou.ui.TimeRange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Preview(showBackground = true)
@Composable
fun PreviewBody() {
    val scd = DemoDataProvider.getNextSchoolday()
    val selectedIndex = remember { mutableStateOf(0) }
    if (scd != null) {
        SchooldayDetailBody(scd, selectedIndex, rememberNavController())
    }
}

@Composable
fun SchooldayDetailBody(
    item: Schoolday,
    selectedIndex: MutableState<Int>,
    navController: NavHostController
) {
    Column {
        NavigationBar(modifier = Modifier.height(48.dp), containerColor = Color.Transparent) {
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
                        Icons.Filled.Description,
                        contentDescription = stringResource(id = R.string.reports)
                    )
                },
                selected = selectedIndex.value == 1,
                onClick = { selectedIndex.value = 1 }
            )
        }

        DividerItem(modifier = Modifier.height(8.dp))

        if (selectedIndex.value == 0) {
            // 授業
            var isRefreshing by remember { mutableStateOf(false) }
            val refreshState = rememberSwipeRefreshState(isRefreshing)

            SwipeRefresh(
                state = refreshState,
                onRefresh = { isRefreshing = true },
            ) {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(DemoDataProvider.getLessons(item)) {
                        val subject = DemoDataProvider.getSubject(it.subjectId)
                        Column {
                            Row(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("lesson-detail/${it.id}")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp, 0.dp)
                                        .background(
                                            Color(subject.color),
                                            shape = CircleShape
                                        )
                                        .size(24.dp)
                                )

                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                // 時刻
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp, 0.dp)
                                ) {
                                    val tf = DemoDataProvider.getTimeFrameOrNull(it.timeFrame)
                                    if (tf != null) {
                                        TimeRange(start = tf.start, end = tf.end)
                                    }
                                    else{
                                        Text(
                                            text = "${it.timeFrame}時限目",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }

                            DividerItem()
                        }
                    }

                    isRefreshing = false
                }
            }
        } else {
            // レポート
            var isRefreshing by remember { mutableStateOf(false) }
            val refreshState = rememberSwipeRefreshState(isRefreshing);

            SwipeRefresh(
                state = refreshState,
                onRefresh = { isRefreshing = true },
            ) {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(DemoDataProvider.getReports(item)) {
                        val subject = DemoDataProvider.getSubject(it.subjectId)
                        Column {
                            Row(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("report-detail/${it.id}")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp, 0.dp)
                                        .background(
                                            Color(subject.color),
                                            shape = CircleShape
                                        )
                                        .size(24.dp)
                                )

                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Column(
                                    verticalArrangement = Arrangement.Bottom,
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp, 0.dp)
                                ) {
                                    Text(it.name)

                                    ProvideTextStyle(
                                        value = MaterialTheme.typography.bodySmall
                                            .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    ) {
                                        when {
                                            it.isExpired -> Text(
                                                "期限切れ",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            it.isNotSubmitted -> Text("未提出")
                                            it.isSubmitted -> Text("提出済み")
                                        }
                                    }
                                }
                            }

                            DividerItem()
                        }
                    }

                    isRefreshing = false
                }
            }
        }
    }
}

@Composable
private fun DividerItem(modifier: Modifier = Modifier) {
    // TODO (M3): No Divider, replace when available
    androidx.compose.material.Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchooldayDetailScreen(navController: NavHostController, id: Int) {
    val item = DemoDataProvider.getSchoolday(id)
    val selectedIndex = rememberSaveable { mutableStateOf(0) }

    Scaffold(
        floatingActionButton = {
            if (selectedIndex.value == 0) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                    text = { Text("授業を追加") },
                    onClick = { /*TODO*/ }
                )
            } else if (selectedIndex.value == 1) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                    text = { Text("レポートを追加") },
                    onClick = { /*TODO*/ }
                )
            }
        },
        topBar = {
            MediumTopAppBar(
                title = { DateFormat(item.date) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }

                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = stringResource(id = R.string.back)
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
        content = { SchooldayDetailBody(item, selectedIndex, navController) }
    )
}