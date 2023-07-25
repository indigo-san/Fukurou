package com.example.fukurou.ui.details

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.fukurou.viewmodel.LessonDetailViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun LessonDetailScreen(
    navController: NavHostController,
    id: Int = -1,
    isCreating: MutableState<Boolean> = remember { mutableStateOf(false) },
    date: LocalDate = LocalDate.now(),
    viewModel: LessonDetailViewModel = viewModel(factory = LessonDetailViewModel.Factory)
) {
    val scope = rememberCoroutineScope()
    val canCreate = remember { mutableStateOf(false) }
    val item = remember { mutableStateOf<Lesson?>(null) }

    LaunchedEffect(Unit) {
        if (isCreating.value) {
            isCreating.value = true
            item.value =
                viewModel.recentlyUsedLesson?.copy(id = 0, date = date)
                    ?: viewModel.getNewItemEntry(date = date, timeFrame = 1)
        } else {
            item.value = viewModel.getLesson(id).first()
        }
    }

    val subject = remember { mutableStateOf<Subject?>(null) }

    LaunchedEffect(item.value) {
        val lesson = item.value
        canCreate.value = lesson != null && viewModel.canAddLesson(lesson)

        if (lesson != null
            && lesson.subjectId != subject.value?.id
        ) {
            subject.value = viewModel.getSubject(lesson.subjectId).first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(targetState = isCreating.value) { state ->
                        if (state) {
                            Text(stringResource(id = R.string.add_lesson))
                        } else {
                            Text(
                                subject.value?.name ?: stringResource(id = R.string.unknown_subject)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    if (isCreating.value) {
                        TextButton(
                            onClick = {
                                isCreating.value = false
                                if (item.value != null) {
                                    viewModel.addLesson(item.value!!)
                                }
                            },
                            enabled = canCreate.value
                        ) {
                            Text(stringResource(id = R.string.create))
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

            ModalBottomSheetLayout(
                modifier = Modifier.padding(innerPadding),
                sheetContent = {
                    SettingsSection(
                        icon = Icons.Outlined.CheckCircleOutline,
                        onClick = {
                            if (item.value != null) {
                                item.value = item.value!!.copy(state = LessonState.Attend)
                                scope.launch {
                                    viewModel.updateLesson(item.value!!)
                                    sheetState.hide()
                                }
                            }
                        }
                    ) {
                        Text("出席としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.Unpublished,
                        onClick = {
                            if (item.value != null) {
                                item.value = item.value!!.copy(state = LessonState.Absent)
                                scope.launch {
                                    viewModel.updateLesson(item.value!!)
                                    sheetState.hide()
                                }
                            }
                        }
                    ) {
                        Text("欠席としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.HelpOutline,
                        onClick = {
                            if (item.value != null) {
                                item.value = item.value!!.copy(state = LessonState.None)
                                scope.launch {
                                    viewModel.updateLesson(item.value!!)
                                    sheetState.hide()
                                }
                            }
                        }
                    ) {
                        Text("未指定としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.DeleteOutline,
                        onClick = {
                            if (item.value != null) {
                                scope.launch {
                                    viewModel.deleteLesson(item.value!!)
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
                sheetContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                LessonDetailBody(item, subject, isCreating, {
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
private fun LessonDetailBodyPreview() {
    LessonDetailBody(
        lesson = remember {
            mutableStateOf(
                Lesson(
                    id = 0,
                    date = LocalDate.now(),
                    timeFrame = 1,
                    subjectId = 0,
                )
            )
        },
        subject = remember {
            mutableStateOf(
                Subject(
                    id = 0,
                    name = "AAA",
                    color = 0xffff1744
                )
            )
        },
        onRequestMenu = {},
        isCreating = remember { mutableStateOf(false) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailBody(
    lesson: MutableState<Lesson?>,
    subject: MutableState<Subject?>,
    isCreating: MutableState<Boolean>,
    onRequestMenu: () -> Unit,
    viewModel: LessonDetailViewModel = viewModel(factory = LessonDetailViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val updateLesson = {
        if (!isCreating.value && lesson.value != null) {
            coroutineScope.launch {
                viewModel.updateLesson(lesson.value!!)
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
            var subjects by remember { mutableStateOf(emptyList<Subject>()) }

            LaunchedEffect(Unit) {
                subjects = viewModel.getSubjects().first()
            }

            SettingsSection(
                icon = Icons.Outlined.Book,
                title = { Text("教科") },
                content = {
                    Text(
                        subject.value?.name ?: stringResource(id = R.string.unknown_subject),
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

                    for (item in subjects) {
                        val select = {
                            if (lesson.value != null) {
                                lesson.value = lesson.value!!.copy(subjectId = item.id)
                                subject.value = item
                                updateLesson()
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
                        lesson.value?.date?.format(dateformatter) ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    if (activity != null && lesson.value != null) {
                        showDatePicker(lesson.value!!.date, activity) {
                            lesson.value = lesson.value!!.copy(date = it)
                            updateLesson()
                        }
                    }
                }
            )

            val timeFramesVisible = remember { mutableStateOf(false) }

            SettingsSection(
                icon = Icons.Outlined.AccessTime,
                title = { Text("時間") },
                content = {
                    Text(
                        text = "${lesson.value?.timeFrame ?: 0}時限目",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = { timeFramesVisible.value = !timeFramesVisible.value }
            )

            var lessons by remember { mutableStateOf(emptyList<Lesson>()) }
            var timeFrames by remember { mutableStateOf(emptyList<TimeFrame>()) }
            LaunchedEffect(lesson.value) {
                if (lesson.value != null) {
                    lessons = viewModel.getLessons(lesson.value!!.date).first()
                }
            }
            LaunchedEffect(Unit) {
                timeFrames = viewModel.getFrames().first()
            }

            AnimatedVisibility(
                visible = timeFramesVisible.value,
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
                    for (item in timeFrames) {
                        val select = {
                            if (lesson.value != null) {
                                lesson.value = lesson.value!!.copy(timeFrame = item.number)
                                updateLesson()
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clickable(onClick = select)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = item.number == lesson.value?.timeFrame,
                                onClick = select
                            )

                            Text("${item.number}時限目")
                        }
                    }
                }
            }

            Divider()

            val roomInputShow = remember { mutableStateOf(false) }
            SettingsSection(
                icon = Icons.Outlined.Room,
                title = { Text("教室") },
                content = {
                    if (lesson.value?.room?.isNotBlank() == true) {
                        Text(
                            lesson.value?.room ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                onClick = {
                    roomInputShow.value = true
                }
            )

            TextInputDialog(
                showDialog = roomInputShow,
                text = lesson.value?.room ?: "N/A",
                title = "教室名を入力",
                label = "教室名"
            ) {
                if (lesson.value != null) {
                    lesson.value = lesson.value!!.copy(room = it)
                    updateLesson()
                }
            }

            val tagInputShow = remember { mutableStateOf(false) }
            SettingsSection(
                icon = Icons.Outlined.Tag,
                title = { Text("タグ") },
                content = {
                    if (lesson.value?.tag?.isNotBlank() == true) {
                        Text(
                            lesson.value!!.tag,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                onClick = {
                    tagInputShow.value = true
                }
            )

            TextInputDialog(
                showDialog = tagInputShow,
                text = lesson.value?.tag ?: "",
                title = "タグを入力",
                label = "タグ"
            ) {
                if (lesson.value != null) {
                    lesson.value = lesson.value!!.copy(tag = it)
                    updateLesson()
                }
            }
        }

        if (!isCreating.value) {
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = when (lesson.value?.state) {
                            LessonState.None -> "状態: 未指定"
                            LessonState.Attend -> "状態: 出席"
                            LessonState.Absent -> "状態: 欠席"
                            null -> "状態: N/A"
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
}