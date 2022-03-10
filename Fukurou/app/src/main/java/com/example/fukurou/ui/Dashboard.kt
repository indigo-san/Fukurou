package com.example.fukurou.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.dateformatter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.absoluteValue

@ExperimentalMaterial3Api
@Composable
fun DashboardContent(
    fabExpandState: MutableState<Boolean>,
    drawerState: DrawerState,
    navController: NavHostController,
    showUserDialog: MutableState<Boolean>
) {
    val lazyListState = rememberLazyListState()

    val offsetY = remember { mutableStateOf(0) }
    val oldIndex = remember { mutableStateOf(0) }
    val searchOffsetY = remember { mutableStateOf(0) }

    val searchLayoutHeightPx = with(LocalDensity.current) { 70.dp.toPx() }

    // ensures that the user intents to have scroll gesture..
    val isVisibleScrolled =
        oldIndex.value != lazyListState.firstVisibleItemIndex ||
                (offsetY.value - lazyListState.firstVisibleItemScrollOffset).absoluteValue > 15

    println("${lazyListState.firstVisibleItemIndex}  ${lazyListState.firstVisibleItemScrollOffset}")

    if (isVisibleScrolled) {
        when {
            oldIndex.value > lazyListState.firstVisibleItemIndex -> {   // down
                fabExpandState.value = true
            }
            oldIndex.value < lazyListState.firstVisibleItemIndex -> {  // up
                fabExpandState.value = false
            }
            oldIndex.value == lazyListState.firstVisibleItemIndex -> {
                fabExpandState.value = offsetY.value > lazyListState.firstVisibleItemScrollOffset
            }
        }

        // for the initial search offset
        if (lazyListState.firstVisibleItemIndex == 0
            && lazyListState.firstVisibleItemScrollOffset < searchLayoutHeightPx
            && !fabExpandState.value
        ) {
            searchOffsetY.value = -lazyListState.firstVisibleItemScrollOffset
        } else if (fabExpandState.value) {
            searchOffsetY.value = 0
        } else if (!fabExpandState.value) {
            searchOffsetY.value = (-searchLayoutHeightPx).toInt()
        }

    }

    offsetY.value = lazyListState.firstVisibleItemScrollOffset
    oldIndex.value = lazyListState.firstVisibleItemIndex
    var isRefreshing by remember { mutableStateOf(false) }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { isRefreshing = true },
    ) {
        Box {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxHeight()
            ) {

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }

                item {
                    NextCard(navController)
                }
            }

            SearchLayout(searchOffsetY.value, drawerState, showUserDialog) {
                navController.navigate("create")
            }
        }

        if (isRefreshing) {
            Text("Refreshing")
        }
        isRefreshing = false
    }
}

@ExperimentalMaterial3Api
@Composable
fun NextCard(navController: NavHostController) {
    val searchLayoutHeightDp = 64.dp
    //val background = if (AppThemeState.darkTheme) graySurface else Color.White.copy(alpha = 0.8f)

    val schoolday = remember { DemoDataProvider.getNextSchoolday() }

    if (schoolday == null) {

    } else {
        SecondaryCardScaffold(
            title = { Text("次の授業") },
            action = {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = stringResource(id = R.string.cd_gmail_menu)
                )
            },
            isClickable = true,
            onClick = {
                navController.navigate("schoolday-detail/${schoolday.id}")
            }
        ) {
            Row {
                DateFormat(schoolday.date)

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .fillMaxWidth()
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
}

@Composable
fun DateFormat(date: LocalDate, modifier: Modifier = Modifier) {
    val now = LocalDate.now()
    Row(modifier = modifier) {
        // 日数が31以内
        val sub = date.toEpochDay() - now.toEpochDay()
        val subAbs = abs(sub)

        if (subAbs <= 31) {
            // 過去
            when {
                date < now -> Text(
                    "${subAbs}日前",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                date == now -> Text("本日", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                else -> Text("${subAbs}日後", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(date.format(dateformatter), modifier = Modifier.align(Alignment.Bottom))
        } else {
            Text(date.format(dateformatter), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun UserEmailDialog(showUserDialog: MutableState<Boolean>) {
    if (showUserDialog.value) {
        Dialog(
            onDismissRequest = {
                showUserDialog.value = false
            }
        ) {

            Surface(
                modifier = Modifier,
//                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {

                Column {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showUserDialog.value = false }) {
                            Icon(Icons.Outlined.Close, contentDescription = null)
                        }

                        Text(
                            text = "Google",
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                    }

//                    GmailUserEmail(R.drawable.p1, "Subash Aryc", "subash@gmail.com", 2)

                    Text(
                        text = "Manage your Google Account",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(8.dp)
                            .border(
                                1.dp,
                                Color.Gray.copy(alpha = 0.6f),
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = {})
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

//                    GmailUserEmail(
//                        imageId = R.drawable.p2,
//                        name = "Subash ",
//                        email = "aryal.subash@yahoo.com",
//                        badgeCount = 39
//                    )
//                    GmailUserEmail(
//                        imageId = R.drawable.p2,
//                        name = "Subash Zi ",
//                        email = "subashz@gmail.com",
//                        badgeCount = 10
//                    )


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(8.dp),
                            contentDescription = null
                        )

                        Text(
                            text = "Add another account",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(8.dp),
                            contentDescription = null
                        )
                        Text(
                            text = "Manage accounts on this device",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Privacy Policy",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = {})
                                .padding(8.dp)
                        )
                        Text(
                            text = "•"
                        )
                        Text(
                            text = "Terms of service",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = {})
                                .padding(8.dp)

                        )
                    }

                }
            }
        }

    }

}

@Composable
fun GmailUserEmail(imageId: Int, name: String, email: String, badgeCount: Int) {

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = {

                })
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(text = name)

            Row {
                Text(
                    text = email,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "$badgeCount",
                    fontSize = 12.sp
                )
            }
        }


    }
}