package com.example.fukurou.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Unpublished
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Report
import com.example.fukurou.data.ReportState
import com.example.fukurou.dateformatter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ReportsContent(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val lazyListState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }
    val items = remember {
        DemoDataProvider.reports
            .sortedBy { it.date }
            .toMutableStateList()
    }
    val context = LocalContext.current

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { isRefreshing = true },
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxHeight()
        ) {
            var lastReport: Report? = null

            for (it in items) {
                if (lastReport?.date != it.date) {
                    stickyHeader {
                        Text(
                            text = it.date.format(dateformatter),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp, 16.dp, 4.dp)
                        )
                    }
                }

                item(it.id) {
                    var deleting by rememberSaveable { mutableStateOf(false) }
                    val dismissState = rememberDismissState()
                    val height by animateDpAsState(
                        if (deleting) 0.dp else 80.dp,
                        animationSpec = tween()
                    )

                    if (dismissState.currentValue != DismissValue.Default) {
                        LaunchedEffect(LocalContext.current) {
                            if (dismissState.currentValue == DismissValue.DismissedToStart && !deleting) {
                                try {
                                    deleting = true

                                    val result = snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.item_was_deleted),
                                        actionLabel = context.getString(R.string.restore)
                                    )

                                    if (result == SnackbarResult.ActionPerformed) {
                                        dismissState.snapTo(DismissValue.Default)
                                        deleting = false
                                    } else {
                                        DemoDataProvider.deleteReport(it.id)
                                        items.remove(it)
                                    }
                                } catch (e: CancellationException) {
                                    DemoDataProvider.deleteReport(it.id)
                                    items.remove(it)
                                }
                            } else if (dismissState.currentValue == DismissValue.DismissedToEnd) {
                                val report =
                                    it.copy(state = if (it.isSubmitted) ReportState.NotSubmitted else ReportState.Submitted)
                                DemoDataProvider.updateReport(report)
                                val index = items.indexOfFirst { it.id == report.id }
                                if (index >= 0) {
                                    items[index] = report
                                }
                                dismissState.animateTo(DismissValue.Default)
                            }
                        }
                    }

                    SwipeToDismiss(
                        modifier = Modifier
                            .padding(8.dp, 0.dp)
                            .height(height)
                            .fillMaxWidth(),
                        state = dismissState,
                        dismissThresholds = { FractionalThreshold(0.5f) },
                        background = {
                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                            val color by animateColorAsState(
                                when (direction) {
                                    DismissDirection.StartToEnd -> {
                                        if (it.isSubmitted)
                                            Color.Green
                                        else
                                            Color.Green
                                    }
                                    DismissDirection.EndToStart -> MaterialTheme.colorScheme.error
                                }
                            )
                            val alignment = when (direction) {
                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                DismissDirection.EndToStart -> Alignment.CenterEnd
                            }
                            val icon = when (direction) {
                                DismissDirection.StartToEnd -> {
                                    if (it.isSubmitted)
                                        Icons.Outlined.Unpublished
                                    else
                                        Icons.Outlined.CheckCircleOutline
                                }
                                DismissDirection.EndToStart -> Icons.Outlined.Delete
                            }
                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = color
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .padding(horizontal = 20.dp),
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .scale(scale)
                                            .align(alignment)
                                    )
                                }
                            }
                        },
                        dismissContent = {
                            Item(it, navController)
                        }
                    )
                }

                lastReport = it
            }

            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        isRefreshing = false
    }

    LaunchedEffect(Unit) {
//        val next = DemoDataProvider.getNextSchoolday()
//        var index = DemoDataProvider.schooldays.indexOf(next)
//        if (index > 0) index--
//
//        lazyListState.scrollToItem(index)
    }
}

@Composable
private fun Item(report: Report, navController: NavHostController) {
    val subject = DemoDataProvider.getSubject(report.subjectId)

    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("report-detail/${report.id}")
                }
                .padding(8.dp),
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
                Text(report.name)

                ProvideTextStyle(
                    value = MaterialTheme.typography.bodySmall
                        .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    when {
                        report.isExpired -> Text(
                            "期限切れ",
                            color = MaterialTheme.colorScheme.error
                        )
                        report.isNotSubmitted -> Text("未提出")
                        report.isSubmitted -> Text("提出済み")
                    }
                }
            }
        }
    }
}