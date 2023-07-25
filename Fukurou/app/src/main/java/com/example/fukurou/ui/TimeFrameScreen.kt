package com.example.fukurou.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.timeformatter
import com.example.fukurou.viewmodel.TimeFrameScreenStatus
import com.example.fukurou.viewmodel.TimeFrameScreenViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TimeFrameScreen(
    navController: NavHostController,
    viewModel: TimeFrameScreenViewModel = viewModel(factory = TimeFrameScreenViewModel.Factory)
) {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("校時表")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("追加") },
                onClick = {
                    scope.launch {
                        viewModel.startAdd()
                        sheetState.show()
                    }
                }
            )
        },
        content = { padding ->
            ModalBottomSheetLayout(
                sheetContent = {
                    val number = viewModel.number.collectAsState()
                    val startTime = viewModel.startTime.collectAsState()
                    val endTime = viewModel.endTime.collectAsState()
                    val status = viewModel.status.collectAsState()

                    val numberInputShow = remember { mutableStateOf(false) }
                    SettingsSection(
                        icon = Icons.Outlined.Tag,
                        title = { Text("${number.value}時限目") },
                        onClick = {
                            numberInputShow.value = true
                        }
                    )

                    TextInputDialog(
                        showDialog = numberInputShow,
                        text = number.value.toString(),
                        title = "N時限目を入力",
                        label = "N時限目",
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    ) {
                        val num = it.toIntOrNull()
                        if (num != null) {
                            viewModel.setNumber(num)
                        }
                    }

                    TimePickSection(
                        title = "開始時刻",
                        showIcon = true,
                        time = startTime
                    ) {
                        viewModel.setStartTime(it)
                    }

                    TimePickSection(
                        title = "終了時刻",
                        showIcon = false,
                        time = endTime
                    ) {
                        viewModel.setEndTime(it)
                    }

                    Spacer(modifier = Modifier.height(88.dp))

                    SettingsSection(
                        icon = if (status.value == TimeFrameScreenStatus.Add) Icons.Outlined.Add else Icons.Outlined.Done,
                        title = { Text(if (status.value == TimeFrameScreenStatus.Add) "追加" else "適用") },
                        onClick = {
                            if (status.value == TimeFrameScreenStatus.Add) {
                                viewModel.endAdd()
                            } else {
                                viewModel.endEdit()
                            }
                            scope.launch {
                                sheetState.hide()
                            }
                        })
                },
                sheetState = sheetState,
                sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                sheetContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(padding)
            ) {
                val isRefreshing by viewModel.isRefreshing.collectAsState()
                val refreshState = rememberSwipeRefreshState(isRefreshing)
                val frames by viewModel.frames.collectAsState()

                SwipeRefresh(
                    state = refreshState,
                    onRefresh = {
                        viewModel.refresh()
                    },
                ) {
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(frames) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .height(56.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.startEdit(it)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = "${it.number}時限目",
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
                                        TimeRange(start = it.start, end = it.end)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TimePickSection(
    title: String,
    showIcon: Boolean,
    time: State<LocalTime>,
    picked: (LocalTime) -> Unit
) {
    val activity = LocalContext.current as? FragmentActivity

    SettingsSection(
        icon = if (showIcon) Icons.Outlined.AccessTime else null,
        title = { Text(title) },
        content = {
            Text(
                text = time.value.format(timeformatter),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        onClick = {
            if (activity != null) {
                showTimePicker(
                    time = time.value,
                    activity = activity,
                    title = title,
                    onClick = {
                        picked(it)
                    }
                )
            }
        }
    )
}