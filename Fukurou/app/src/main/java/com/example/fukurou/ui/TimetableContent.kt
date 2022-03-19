package com.example.fukurou.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Schoolday
import com.example.fukurou.dateformatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.absoluteValue

@ExperimentalFoundationApi
@Composable
fun TimetableContent(navController: NavHostController) {
    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .padding(bottom = 88.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(0.dp, 40.dp, 0.dp, 0.dp)
                .verticalScroll(scrollState)
        ) {
            for (index in 0..24) {
                Column(
                    modifier = Modifier
                        .size(32.dp, 48.dp)
                ) {
                    Text(index.toString())
                }
            }
        }

        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            var last: Schoolday? = null

            for (it in DemoDataProvider.schooldays) {
                if (last != null) {
                    val sub = it.date.toEpochDay() - last.date.toEpochDay()
                    val date = last.date

                    for (offset in 1 until sub) {
                        item {
                            Column {
                                val date2 = date.plusDays(offset)
                                TimetableHeader(
                                    modifier = Modifier
                                        .graphicsLayer(translationY = scrollState.value.toFloat()),
                                    week = date2.dayOfWeek,
                                    date = date2
                                )

                                Box(modifier = Modifier.height((48 * 24).dp))
                            }
                        }
                    }
                }

                item {
                    Column {
                        TimetableHeader(
                            modifier = Modifier.graphicsLayer(translationY = scrollState.value.toFloat()),
                            week = it.date.dayOfWeek,
                            date = it.date
                        )

                        Box(modifier = Modifier.height((48 * 24).dp))
                    }
                }

                last = it
            }
        }
    }

//    LazyColumn(
//        state = lazyListState,
//        modifier = Modifier.fillMaxHeight()
//    ) {
//        for (item in DemoDataProvider.chunkSchooldays()) {
//            stickyHeader {
//                Row(
//                    modifier = Modifier
//                        .padding(32.dp, 0.dp, 0.dp, 0.dp)
//                        .horizontalScroll(scrollState)
//                ) {
//                    TimetableHeader(DayOfWeek.MONDAY, item.monday?.date)
//                    TimetableHeader(DayOfWeek.TUESDAY, item.tuesday?.date)
//                    TimetableHeader(DayOfWeek.WEDNESDAY, item.wednesday?.date)
//                    TimetableHeader(DayOfWeek.THURSDAY, item.thursday?.date)
//                    TimetableHeader(DayOfWeek.FRIDAY, item.friday?.date)
//                    TimetableHeader(DayOfWeek.SATURDAY, item.saturday?.date)
//                    TimetableHeader(DayOfWeek.SUNDAY, item.sunday?.date)
//                }
//            }
//
//            item {
//                Row {
//                    Column(modifier = Modifier.width(32.dp)) {
//
//                    }
//
//                    Column(
//                        modifier = Modifier
//                            .size(64.dp, 40.dp)
//                            .padding(1.dp, 0.dp)
//                    ) {
//                        val schoolday = item.monday
//                        if (schoolday != null) {
//                            val lessons = DemoDataProvider.getLessons(schoolday)
//
//
//                        }
//                    }
//
//
//                }
//            }
//        }
//    }
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
        modifier = Modifier
            .size(64.dp, 40.dp)
            .padding(1.dp, 0.dp)
            .clickable(onClick = onClick)
            .then(modifier)
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