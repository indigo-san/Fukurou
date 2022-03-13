package com.example.fukurou.ui.details

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.*
import com.example.fukurou.dateformatter
import com.example.fukurou.timeformatter
import com.example.fukurou.ui.SettingsSection
import com.example.fukurou.ui.TextInputDialog
import com.example.fukurou.ui.showDatePicker
import com.example.fukurou.ui.showTimePicker
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun ReportDetailScreen(navController: NavHostController, id: Int) {
    val item = remember { mutableStateOf(DemoDataProvider.getReport(id)) };
    val subject = DemoDataProvider.getSubject(item.value.subjectId)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("${subject.name} ${item.value.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back)
                        )
                    }
                },
                actions = { }
            )
        },
        content = {
            val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
            val scope = rememberCoroutineScope()

            ModalBottomSheetLayout(
                sheetContent = {
                    SettingsSection(
                        icon = Icons.Outlined.CheckCircleOutline,
                        onClick = {
                            item.value = item.value.copy(state = ReportState.Submitted)
                            DemoDataProvider.updateReport(item.value)
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    ) {
                        Text("提出済みとしてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.Unpublished,
                        onClick = {
                            item.value = item.value.copy(state = ReportState.NotSubmitted)
                            DemoDataProvider.updateReport(item.value)
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    ) {
                        Text("未提出としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.DeleteOutline,
                        onClick = {
                            DemoDataProvider.deleteReport(item.value.id)
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(id = R.string.delete))
                    }
                },
                sheetState = sheetState,
                sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                sheetContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                ReportDetailBody(item) {
                    scope.launch {
                        sheetState.show()
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@ExperimentalMaterial3Api
@Composable
private fun ReportDetailBodyPreview() {
    ReportDetailBody(
        report = remember { mutableStateOf(DemoDataProvider.reports[0]) },
        onRequestMenu = {})
}

@ExperimentalMaterial3Api
@Composable
fun ReportDetailBody(report: MutableState<Report>, onRequestMenu: () -> Unit) {
    var subject = DemoDataProvider.getSubject(report.value.subjectId)

    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val subjectsVisible = remember { mutableStateOf(false) }
            val nameInputShow = remember { mutableStateOf(false) }

            SettingsSection(
                icon = Icons.Outlined.Label,
                title = { Text("名前") },
                content = { Text(report.value.name, style = MaterialTheme.typography.titleLarge) },
                onClick = { nameInputShow.value = true }
            )

            TextInputDialog(
                showDialog = nameInputShow,
                text = report.value.name,
                title = "名前を入力",
                label = "名前"
            ) {
                report.value = report.value.copy(name = it)
                DemoDataProvider.updateReport(report.value)
            }

            SettingsSection(
                icon = Icons.Outlined.Book,
                title = { Text("教科") },
                content = { Text(subject.name, style = MaterialTheme.typography.titleLarge) },
                onClick = {
                    subjectsVisible.value = !subjectsVisible.value
                }
            )

            val density = LocalDensity.current
            AnimatedVisibility(
                visible = subjectsVisible.value,
                enter = slideInVertically {
                    // Slide in from 40 dp from the top.
                    with(density) { -40.dp.roundToPx() }
                } + expandVertically(
                    // Expand from the top.
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    // Fade in with the initial alpha of 0.3f.
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                Column {
                    for (item in DemoDataProvider.subjects) {
                        val select = {
                            report.value = report.value.copy(subjectId = item.id)
                            subject = item
                            DemoDataProvider.updateReport(report.value)
                        }

                        Row(
                            modifier = Modifier
                                .clickable(onClick = select)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = item.id == subject.id,
                                onClick = select
                            )

                            Text(item.name)
                        }
                    }
                }
            }

            val activity = LocalContext.current as? FragmentActivity

            SettingsSection(
                icon = Icons.Outlined.EditCalendar,
                title = { Text("日付") },
                content = {
                    Text(
                        report.value.date.format(dateformatter),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    if (activity != null) {
                        showDatePicker(report.value.date, activity) {
                            report.value = report.value.copy(date = it)
                            DemoDataProvider.updateReport(report.value)
                        }
                    }
                }
            )
        }

        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = when {
                        report.value.isExpired -> "状態: 期限切れ"
                        report.value.isSubmitted -> "状態: 提出済み"
                        report.value.isNotSubmitted -> "状態: 未提出"
                        else -> "状態: 不明"
                    }
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onRequestMenu() }
                ) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                }
            }
        }
    }
}
