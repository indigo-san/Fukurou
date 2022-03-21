package com.example.fukurou.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.*
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Lesson
import com.example.fukurou.dateformatter
import com.example.fukurou.totalHour
import com.google.accompanist.pager.ExperimentalPagerApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

// 週の最初の日付を取得します
private fun getCurrentWeek(): LocalDate {
    val now = LocalDate.now()
    return now.minusDays(now.dayOfWeek.ordinal.toLong())
}

private enum class SwipeDirection {
    None,
    LeftToRight,
    RightToLeft
}

private val HourHeight = 72.dp//48.dp
private val DayHeight = HourHeight * 24

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TimetableContent(navController: NavHostController) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var base by remember { mutableStateOf(getCurrentWeek()) }
    val state = rememberSwipeableState(SwipeDirection.None) {
        base = when (it) {
            SwipeDirection.None -> base
            SwipeDirection.LeftToRight -> base.minusDays(7)
            SwipeDirection.RightToLeft -> base.plusDays(7)
        }
        true
    }

    val px = with(LocalDensity.current) { 80.dp.toPx() }
    val anchors = mapOf(
        -px to SwipeDirection.RightToLeft,
        0f to SwipeDirection.None,
        px to SwipeDirection.LeftToRight
    )

    Row {
        Column(
            modifier = Modifier
                .zIndex(5f)
                .padding(0.dp, 40.dp, 0.dp, 0.dp)
                .verticalScroll(verticalScrollState)
        ) {
            repeat(24) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .size(32.dp, HourHeight)
                        .padding(top = 8.dp),
                ) {
                    Text(index.toString())
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
                verticalScrollState = verticalScrollState,
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

    val hourHeightPx = with(LocalDensity.current) { HourHeight.toPx() }
    LaunchedEffect(Unit) {
        verticalScrollState.animateScrollTo(
            ((hourHeightPx * LocalTime.now().totalHour) - px).coerceAtLeast(
                0f
            ).toInt()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun Timetable(
    verticalScrollState: ScrollState,
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
                    modifier = Modifier.padding(
                        start = 1.dp,
                        end = if (offset == 6)
                            1.dp
                        else
                            0.dp
                    )
                )
            }
        }

        HorizontalDividerItem()

        Box(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState)
                .height(DayHeight)
        ) {
            repeat(24) { index ->
                Box(
                    modifier = Modifier
                        .padding(top = HourHeight * (index + 1))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .width(66.dp * 7)
                        .height(1.dp)
                )
            }

            Row {
                for (offset in 0..6) {
                    VerticalDividerItem()
                    val d = date.plusDays(offset.toLong())
                    TimetableList(date = d, navController = navController)
                }

                VerticalDividerItem()
            }

            Box(
                modifier = Modifier
                    .padding(top = HourHeight * LocalTime.now().totalHour)
                    .background(MaterialTheme.colorScheme.error)
                    .width(66.dp * 7)
                    .height(1.dp)
            )
        }
    }
}

@Composable
private fun VerticalDividerItem() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxHeight()
            .width(1.dp)
    )
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
private fun RowScope.TimetableList(date: LocalDate, navController: NavHostController) {
    val scd = DemoDataProvider.getSchooldayOrNull(date)
    Box(
        Modifier
            .width(64.dp)
            .fillMaxHeight()
            .padding(2.dp, 0.dp)
    ) {
        if (scd != null) {
            val lessons = DemoDataProvider.getLessons(scd)

            for (it in lessons) {
                val startF = it.start.totalHour
                val endF = it.end.totalHour
                LessonItem(
                    lesson = it,
                    modifier = Modifier
                        .padding(top = HourHeight * startF)
                        .width(64.dp)
                        .height(HourHeight * (endF - startF))
                        .clickable {
                            navController.navigate("lesson-detail/${it.id}")
                        }
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