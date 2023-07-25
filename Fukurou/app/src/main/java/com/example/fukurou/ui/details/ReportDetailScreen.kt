package com.example.fukurou.ui.details

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.*
import com.example.fukurou.dateformatter
import com.example.fukurou.ui.SettingsSection
import com.example.fukurou.ui.TextInputDialog
import com.example.fukurou.ui.showDatePicker
import com.example.fukurou.viewmodel.ReportDetailViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    navController: NavHostController,
    id: Int = -1,
    viewModel: ReportDetailViewModel = viewModel(factory = ReportDetailViewModel.Factory)
) {
    val item = remember { mutableStateOf<Report?>(null) }
    val subject = remember { mutableStateOf<Subject?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        item.value = viewModel.getReport(id).first()
    }

    LaunchedEffect(item.value) {
        if (item.value != null) {
            subject.value = viewModel.getSubject(item.value!!.subjectId).first()
        } else {
            subject.value = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${subject.value?.name ?: stringResource(id = R.string.unknown_subject)} ${item.value?.name ?: "N/A"}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = { }
            )
        },
        content = { padding ->
            val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

            val update: (modify: (item: Report) -> Report) -> Unit = { modify ->
                if (item.value != null) {
                    item.value = modify(item.value!!)
                    scope.launch {
                        viewModel.updateReport(item.value!!)
                        sheetState.hide()
                    }
                }
            }

            ModalBottomSheetLayout(
                sheetContent = {
                    SettingsSection(
                        icon = Icons.Outlined.CheckCircleOutline,
                        onClick = {
                            update {
                                it.copy(state = ReportState.Submitted)
                            }
                        }
                    ) {
                        Text("提出済みとしてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.Unpublished,
                        onClick = {
                            update {
                                it.copy(state = ReportState.NotSubmitted)
                            }
                        }
                    ) {
                        Text("未提出としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.DeleteOutline,
                        onClick = {
                            if (item.value != null) {
                                scope.launch {
                                    viewModel.deleteReport(item.value!!)
                                }
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.delete))
                    }
                },
                sheetState = sheetState,
                sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                sheetContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(padding)
            ) {
                ReportDetailBody(item, subject, {
                    scope.launch {
                        sheetState.show()
                    }
                })
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ReportDetailBodyPreview() {
    ReportDetailBody(
        report = remember { mutableStateOf(DemoDataProvider.reports[0]) },
        subject = remember { mutableStateOf(DemoDataProvider.subjects[0]) },
        onRequestMenu = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailBody(
    report: MutableState<Report?>,
    subject: MutableState<Subject?>,
    onRequestMenu: () -> Unit,
    viewModel: ReportDetailViewModel = viewModel(factory = ReportDetailViewModel.Factory)
) {
    val scope = rememberCoroutineScope()
    val update: (modify: (item: Report) -> Report) -> Unit = { modify ->
        if (report.value != null) {
            report.value = modify(report.value!!)
            scope.launch {
                viewModel.updateReport(report.value!!)
            }
        }
    }

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
                content = {
                    Text(
                        report.value?.name ?: "N/A",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                onClick = { nameInputShow.value = true }
            )

            TextInputDialog(
                showDialog = nameInputShow,
                text = report.value?.name ?: "",
                title = "名前を入力",
                label = "名前"
            ) { str ->
                update {
                    it.copy(name = str)
                }
            }

            SettingsSection(
                icon = Icons.Outlined.Book,
                title = { Text("教科") },
                content = {
                    Text(
                        subject.value?.name ?: stringResource(R.string.unknown_subject),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                    var subjects by remember { mutableStateOf(emptyList<Subject>()) }

                    LaunchedEffect(Unit) {
                        subjects = viewModel.getSubjects().first()
                    }
                    for (item in subjects) {
                        val select: () -> Unit = {
                            update {
                                subject.value = item
                                it.copy(subjectId = item.id)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clickable(onClick = select)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = item.id == subject.value?.id,
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
                        report.value?.date?.format(dateformatter) ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    if (activity != null&&report.value!=null) {
                        showDatePicker(report.value!!.date, activity) { date ->
                            update {
                                it.copy(date = date)
                            }
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
                        report.value?.isExpired==true -> "状態: 期限切れ"
                        report.value?.isSubmitted==true -> "状態: 提出済み"
                        report.value?.isNotSubmitted==true -> "状態: 未提出"
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
