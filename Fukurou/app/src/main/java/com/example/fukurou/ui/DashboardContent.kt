package com.example.fukurou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.dateformatter
import com.example.fukurou.viewmodel.DashboardViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun DashboardContent(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val lazyListState = rememberLazyListState()

    println("${lazyListState.firstVisibleItemIndex}  ${lazyListState.firstVisibleItemScrollOffset}")

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            viewModel.refresh()
        },
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxHeight()
        ) {
            item {
                NextCard(navController)
            }

            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }
    }
}

@Composable
fun NextCard(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val searchLayoutHeightDp = 64.dp
    //val background = if (AppThemeState.darkTheme) graySurface else Color.White.copy(alpha = 0.8f)

    val date by viewModel.nextDay.collectAsState()

    if (date == null) {

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
                navController.navigate("schoolday-detail/${date!!.toEpochDay()}")
            }
        ) {
            Row {
                DateFormat(date!!)

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .fillMaxWidth()
                ) {
                    val dotColors by viewModel.dotColors.collectAsState()
                    var x = 0
                    for (item in dotColors) {

                        Box(
                            modifier = Modifier
                                .padding(x.dp, 0.dp, 0.dp, 0.dp)
                                .size(24.dp, 24.dp)
                                .border(
                                    2.dp,
                                    LocalContentColor.current,
                                    CircleShape
                                )
                                .background(Color(item), shape = CircleShape)
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