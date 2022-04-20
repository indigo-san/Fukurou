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
import androidx.compose.material3.Divider
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
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Lesson
import com.example.fukurou.data.LessonState
import com.example.fukurou.dateformatter
import com.example.fukurou.timeformatter
import com.example.fukurou.ui.SettingsSection
import com.example.fukurou.ui.TextInputDialog
import com.example.fukurou.ui.showDatePicker
import com.example.fukurou.ui.showTimePicker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    androidx.compose.animation.ExperimentalAnimationApi::class
)
@Composable
fun LessonDetailScreen(
    navController: NavHostController,
    id: Int,
    isCreating: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val canCreate = remember { mutableStateOf(false) }
    val item = remember {
        if (isCreating.value) {
            val recently = DemoDataProvider.recentlyUsedLesson
            mutableStateOf(
                recently?.copy(id = DemoDataProvider.getNextLessonId())
                    ?: Lesson(
                        id = DemoDataProvider.getNextLessonId(),
                        date = LocalDate.now(),
                        timeFrame = 0,
                        subjectId = DemoDataProvider.subjects.firstOrNull()?.id ?: -1
                    )
            )
        } else {
            mutableStateOf(DemoDataProvider.getLesson(id))
        }
    }
    canCreate.value = DemoDataProvider.canAddLesson(item.value)
    val subject = DemoDataProvider.getSubjectOrNull(item.value.subjectId)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    AnimatedContent(targetState = isCreating.value) { state ->
                        if (state) {
                            Text(stringResource(id = R.string.add_lesson))
                        } else {
                            Text(subject?.name ?: stringResource(id = R.string.unknown_subject))
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
                                DemoDataProvider.addLesson(item.value)
                            },
                            enabled = canCreate.value
                        ) {
                            Text(stringResource(id = R.string.create))
                        }
                    }
                }
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
                            item.value = item.value.copy(state = LessonState.Attend)
                            DemoDataProvider.updateLesson(item.value)
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    ) {
                        Text("出席としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.Unpublished,
                        onClick = {
                            item.value = item.value.copy(state = LessonState.Absent)
                            DemoDataProvider.updateLesson(item.value)
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    ) {
                        Text("欠席としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.HelpOutline,
                        onClick = {
                            item.value = item.value.copy(state = LessonState.None)
                            DemoDataProvider.updateLesson(item.value)
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    ) {
                        Text("未指定としてマーク")
                    }

                    SettingsSection(
                        icon = Icons.Outlined.DeleteOutline,
                        onClick = {
                            DemoDataProvider.deleteLesson(item.value.id)
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
                LessonDetailBody(item, isCreating) {
                    scope.launch {
                        sheetState.show()
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LessonDetailBodyPreview() {
    LessonDetailBody(
        lesson = remember { mutableStateOf(DemoDataProvider.lessons[0]) },
        onRequestMenu = {},
        isCreating = remember { mutableStateOf(false) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailBody(
    lesson: MutableState<Lesson>,
    isCreating: MutableState<Boolean>,
    onRequestMenu: () -> Unit
) {
    var subject = DemoDataProvider.getSubject(lesson.value.subjectId)

    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val subjectsVisible = remember { mutableStateOf(false) }

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
                            lesson.value = lesson.value.copy(subjectId = item.id)
                            subject = item
                            if (!isCreating.value)
                                DemoDataProvider.updateLesson(lesson.value)
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
                        lesson.value.date.format(dateformatter),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    if (activity != null) {
                        showDatePicker(lesson.value.date, activity) {
                            lesson.value = lesson.value.copy(date = it)
                            if (!isCreating.value)
                                DemoDataProvider.updateLesson(lesson.value)
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
                        text = "${lesson.value.timeFrame}時限目",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = { timeFramesVisible.value = !timeFramesVisible.value }
            )

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
                    for (item in DemoDataProvider.timeFrames.sortedBy { it.number }) {
                        val select = {
                            lesson.value = lesson.value.copy(timeFrame = item.number)
                            if (!isCreating.value)
                                DemoDataProvider.updateLesson(lesson.value)
                        }

                        Row(
                            modifier = Modifier
                                .clickable(onClick = select)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = item.number == lesson.value.timeFrame,
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
                    if (lesson.value.room.isNotBlank()) {
                        Text(
                            lesson.value.room,
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
                text = lesson.value.room,
                title = "教室名を入力",
                label = "教室名"
            ) {
                lesson.value = lesson.value.copy(room = it)
                if (!isCreating.value)
                    DemoDataProvider.updateLesson(lesson.value)
            }

            val tagInputShow = remember { mutableStateOf(false) }
            SettingsSection(
                icon = Icons.Outlined.Tag,
                title = { Text("タグ") },
                content = {
                    if (lesson.value.tag.isNotBlank()) {
                        Text(
                            lesson.value.tag,
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
                text = lesson.value.tag,
                title = "タグを入力",
                label = "タグ"
            ) {
                lesson.value = lesson.value.copy(tag = it)
                if (!isCreating.value)
                    DemoDataProvider.updateLesson(lesson.value)
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
                        text = when (lesson.value.state) {
                            LessonState.None -> "状態: 未指定"
                            LessonState.Attend -> "状態: 出席"
                            LessonState.Absent -> "状態: 欠席"
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