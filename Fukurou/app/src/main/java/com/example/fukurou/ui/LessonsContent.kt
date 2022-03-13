package com.example.fukurou.ui

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Schoolday
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun LessonsContent(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val lazyListState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val items = remember {
        val list = mutableStateListOf<Schoolday>()
        list.addAll(DemoDataProvider.schooldays)
        list
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { isRefreshing = true },
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxHeight()
        ) {
            items(items, key = { it.id }) {
                var deleting by remember { mutableStateOf(false) }
                val dismissState = rememberDismissState(
                    confirmStateChange = { value: DismissValue ->
                        if (value == DismissValue.DismissedToStart || value == DismissValue.DismissedToEnd) {
                            deleting = true
                            val message = stringResource(R.string.item_was_deleted)
                            scope.launch {
                                delay(300)
                                snackbarHostState.showSnackbar(
                                    message = Application
                                )
                                DemoDataProvider.deleteSchoolday(it.id)
                                items.remove(it)
                            }
                        }

                        true
                    }
                )

                val height by animateDpAsState(
                    if (deleting) 0.dp else 80.dp,
                    animationSpec = tween()
                )

                SwipeToDismiss(
                    modifier = Modifier
                        .padding(8.dp, 0.dp)
                        .height(height)
                        .fillMaxWidth()
                        .animateItemPlacement(),
                    state = dismissState,
                    dismissThresholds = { FractionalThreshold(0.5f) },
                    background = {
                        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                        val color = MaterialTheme.colorScheme.error
                        val alignment = when (direction) {
                            DismissDirection.StartToEnd -> Alignment.CenterStart
                            DismissDirection.EndToStart -> Alignment.CenterEnd
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
                                    Icons.Outlined.Delete,
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

            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        isRefreshing = false
    }

//    LaunchedEffect(Unit) {
//        val next = DemoDataProvider.getNextSchoolday()
//        var index = DemoDataProvider.schooldays.indexOf(next)
//        if (index > 0) index--
//
//        lazyListState.scrollToItem(index)
//    }
}

@Composable
@Preview(showBackground = true)
private fun ItemPreview() {
    Item(DemoDataProvider.schooldays[0], rememberNavController())
}

@Composable
private fun Item(schoolday: Schoolday, navController: NavHostController) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    navController.navigate("schoolday-detail/${schoolday.id}")
                }
                .padding(8.dp)
        ) {
            DateFormat(schoolday.date)

            Box(
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .height(24.dp)
            ) {
                var x = 0
                for (item in DemoDataProvider.getLessons(schoolday)
                    .map { DemoDataProvider.getSubject(it.subjectId) }) {

                    Box(
                        modifier = Modifier
                            .padding(x.dp, 0.dp, 0.dp, 0.dp)
                            .size(24.dp, 24.dp)
                            .border(
                                2.dp,
                                LocalContentColor.current,
                                CircleShape
                            )
                            .background(Color(item.color), shape = CircleShape)
                    )

                    x += 12
                }
            }
        }
    }
}