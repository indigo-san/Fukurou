package com.example.fukurou.ui.details

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Lesson
import com.example.fukurou.dateformatter
import com.example.fukurou.timeformatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import com.google.android.material.R as MaterialR

fun ShowDatePicker(date: LocalDate, activity: FragmentActivity, onClick: (LocalDate) -> Unit) {
    val calendar: Calendar = Calendar.getInstance()
    calendar.apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.monthValue - 1)
        set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
    }
    MaterialDatePicker.Builder.datePicker()
        .setSelection(calendar.timeInMillis).build().apply {
            show(activity.supportFragmentManager, "Tag")
            addOnPositiveButtonClickListener {
                val result: LocalDate =
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                onClick(result)
            }
        }
}

fun ShowTimePicker(time: LocalTime, activity: FragmentActivity, onClick: (LocalTime) -> Unit) {
    MaterialTimePicker.Builder()
        .setHour(time.hour)
        .setMinute(time.minute)
        .build().apply {
            show(activity.supportFragmentManager, "Tag")
            addOnPositiveButtonClickListener {
                onClick(LocalTime.of(this.hour, this.minute))
            }
        }
}

@ExperimentalMaterial3Api
@Composable
fun LessonDetailScreen(navController: NavHostController, id: Int) {
    val item = remember { mutableStateOf(DemoDataProvider.getLesson(id)) };
    val subject = DemoDataProvider.getSubject(item.value.subjectId)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(subject.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back)
                        )
                    }

                },
                actions = {
//                    IconButton(onClick = {}) {
//                        Icon(
//                            imageVector = Icons.Outlined.Archive,
//                            contentDescription = stringResource(id = R.string.cd_back)
//                        )
//                    }
//                    IconButton(onClick = {}) {
//                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
//                    }
//                    IconButton(onClick = {}) {
//                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
//
//                    }
                }
            )
        },
        content = { LessonDetailBody(item) }
    )
}

@ExperimentalMaterial3Api
@Composable
fun LessonDetailBody(lesson: MutableState<Lesson>) {
    var subject = DemoDataProvider.getSubject(lesson.value.subjectId)

    Column {
        val subjectsVisible = remember { mutableStateOf(false) }

        Section(
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

        val activity = LocalContext.current as FragmentActivity

        Section(
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
                ShowDatePicker(lesson.value.date, activity) {
                    lesson.value = lesson.value.copy(date = it)
                    DemoDataProvider.updateLesson(lesson.value)
                }
            }
        )

        Section(
            icon = Icons.Outlined.AccessTime,
            title = { Text("開始時間") },
            content = {
                Text(
                    lesson.value.start.format(timeformatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                ShowTimePicker(lesson.value.start, activity) {
                    lesson.value = lesson.value.copy(start = it)
                    DemoDataProvider.updateLesson(lesson.value)
                }
            }
        )

        Section(
            title = { Text("終了時間") },
            content = {
                Text(
                    lesson.value.end.format(timeformatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                ShowTimePicker(lesson.value.start, activity) {
                    lesson.value = lesson.value.copy(end = it)
                    DemoDataProvider.updateLesson(lesson.value)
                }
            }
        )

        Divider()

        val roomInputShow = remember { mutableStateOf(false) }
        Section(
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

        TextInputDialog(roomInputShow, lesson.value.room) {
            lesson.value = lesson.value.copy(room = it)
            DemoDataProvider.updateLesson(lesson.value)
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun DialogPreview() {
    Card(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column() {
            TextField(value = "", onValueChange = { })

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth()
            ) {
                TextButton(onClick = { }) {
                    Text("Cancel")
                }

                TextButton(
                    onClick =
                    {
                    }) {
                    Text("OK")
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun TextInputDialog(
    showDialog: MutableState<Boolean>,
    text: String,
    callback: (String) -> Unit
) {
    val value = remember { mutableStateOf(text) }
    if (showDialog.value) {
        Dialog(
            onDismissRequest = {
                showDialog.value = false
            }
        ) {
            Card(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column {
                    TextField(value = value.value, onValueChange = { value.value = it })

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text(stringResource(id = MaterialR.string.mtrl_picker_cancel))
                        }

                        TextButton(
                            onClick =
                            {
                                showDialog.value = false
                                callback(value.value)
                            }) {
                            Text(stringResource(id = MaterialR.string.mtrl_picker_confirm))
                        }
                    }
                }
            }
        }

    }

}


@ExperimentalMaterial3Api
@Composable
private fun Section(
    title: @Composable () -> Unit = {},
    onClick: () -> Unit,
    icon: ImageVector? = null,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon ?: ImageVector.Builder(
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).build(),
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        ) {
            title()
            content()
        }

    }
}