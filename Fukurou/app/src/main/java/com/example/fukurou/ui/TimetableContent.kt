package com.example.fukurou.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.*
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.fukurou.R
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Lesson
import com.example.fukurou.dateformatter
import com.example.fukurou.totalHour
import com.google.accompanist.pager.ExperimentalPagerApi
import de.charlex.compose.SpeedDialData
import de.charlex.compose.SpeedDialFloatingActionButton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

// 週の最初の日付を取得します
private fun getThisWeek(): LocalDate {
    val now = LocalDate.now()
    return now.minusDays(now.dayOfWeek.ordinal.toLong())
}

private enum class SwipeDirection {
    None,
    LeftToRight,
    RightToLeft
}

private val DayWidth = 64.dp

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TimetableContent(navController: NavHostController, weekStart: MutableState<LocalDate>) {
    val px = with(LocalDensity.current) { 80.dp.toPx() }

    val horizontalScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var base by weekStart
    val state = rememberSwipeableState(SwipeDirection.None) {
        base = when (it) {
            SwipeDirection.None -> base
            SwipeDirection.LeftToRight -> base.minusDays(7)
            SwipeDirection.RightToLeft -> base.plusDays(7)
        }
        true
    }

    val anchors = mapOf(
        -px to SwipeDirection.RightToLeft,
        0f to SwipeDirection.None,
        px to SwipeDirection.LeftToRight
    )

    Box(Modifier.fillMaxHeight()) {
        Row {
            Column(
                modifier = Modifier
                    .zIndex(5f)
                    .padding(0.dp, 40.dp, 0.dp, 0.dp)
            ) {
                for (item in DemoDataProvider.timeFrames) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(32.dp)
                            .weight(1f)
                            .padding(top = 8.dp),
                    ) {
                        Text("${item.number}時限目")
                    }
                }
            }

            AnimatedContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                targetState = base,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() with
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() with
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                }
            ) { date ->
                Timetable(
                    horizontalScrollState = horizontalScrollState,
                    navController = navController,
                    date = date,
                    headerModifier = Modifier
                        .swipeable(
                            state = state,
                            anchors = anchors,
                            orientation = Orientation.Horizontal
                        )
                )
            }
        }

        val activity = LocalContext.current as? FragmentActivity

        SpeedDialFloatingActionButton(
            onClick = { speedDialData: SpeedDialData? ->
                when (speedDialData?.name) {
                    "move_to_this_week" -> base = getThisWeek()
                    "add_lesson" -> navController.navigate("create-lesson")
                    null -> {}
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            showLabels = true,
            fabBackgroundColor = MaterialTheme.colorScheme.secondary,
            fabContentColor = MaterialTheme.colorScheme.onSecondary,
            speedDialBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            speedDialContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            speedDialData = listOf(
                SpeedDialData(
                    name = "move_to_this_week",
                    label = stringResource(id = R.string.move_to_this_week),
                    painter = rememberVectorPainter(Icons.Outlined.CalendarToday)
                ),
                SpeedDialData(
                    name = "add_lesson",
                    label = stringResource(id = R.string.add_lesson),
                    painter = rememberVectorPainter(Icons.Outlined.Class)
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun Timetable(
    horizontalScrollState: ScrollState,
    date: LocalDate,
    navController: NavHostController,
    headerModifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .then(headerModifier)
        ) {
            for (offset in 0..6) {
                val d = date.plusDays(offset.toLong())
                TimetableHeader(
                    week = d.dayOfWeek,
                    date = d,
                    onClick = {
                        navController.navigate("schoolday-detail/${d.toEpochDay()}")
                    }
                )
            }
        }

        HorizontalDividerItem()

        Box(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .fillMaxHeight()
        ) {
            val lineColor = MaterialTheme.colorScheme.surfaceVariant
            val nowLineColor = MaterialTheme.colorScheme.error

            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(DayWidth * 7)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val hourHeightPx = canvasHeight / DemoDataProvider.timeFrames.size
                val dayWidthPx = DayWidth.toPx()
                val strokePx = 1.dp.toPx()

                //横線を描画
                for (index in 1..DemoDataProvider.timeFrames.size) {
                    val posY = hourHeightPx * index

                    drawLine(
                        start = Offset(x = 0f, y = posY),
                        end = Offset(x = canvasWidth, y = posY),
                        color = lineColor,
                        strokeWidth = strokePx
                    )
                }

                //縦線
                for (index in 0..7) {
                    val posX = dayWidthPx * index

                    drawLine(
                        start = Offset(x = posX, y = 0f),
                        end = Offset(x = posX, y = canvasHeight),
                        color = lineColor,
                        strokeWidth = strokePx
                    )
                }
//
//                val posY = hourHeightPx * LocalTime.now().totalHour
//                drawLine(
//                    start = Offset(x = 0f, y = posY),
//                    end = Offset(x = canvasWidth, y = posY),
//                    color = nowLineColor,
//                    strokeWidth = strokePx
//                )
            }

            Row {
                for (offset in 0..6) {
                    val d = date.plusDays(offset.toLong())
                    TimetableList(date = d, navController = navController)
                }
            }
        }
    }
}

@Composable
private fun HorizontalDividerItem() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .height(1.dp)
    )
}

@Composable
private fun RowScope.TimetableList(
    date: LocalDate,
    navController: NavHostController
) {
    Column(
        Modifier
            .width(DayWidth)
            .fillMaxHeight()
            .padding(2.dp, 0.dp)
    ) {
        val lessons = DemoDataProvider.getLessons(date)

        for (tf in DemoDataProvider.timeFrames.sortedBy { it.number }) {
            val it = lessons.firstOrNull { it.timeFrame == tf.number }
            if (it != null) {
                LessonItem(
                    lesson = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(0.dp, 2.dp)
                        .clickable {
                            navController.navigate("lesson-detail/${it.id}")
                        }
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 2.dp)
                )
            }
        }
    }
}

@Composable
private fun LessonItem(lesson: Lesson, modifier: Modifier = Modifier) {
    val subject = DemoDataProvider.getSubject(lesson.subjectId)
    Surface(
        color = Color(subject.color),
        modifier = modifier,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = subject.name,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun TimetableHeader(
    modifier: Modifier = Modifier,
    week: DayOfWeek,
    date: LocalDate? = null,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .size(64.dp, 40.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = week.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        )

        if (date != null) {
            Text(
                text = date.format(dateformatter),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}