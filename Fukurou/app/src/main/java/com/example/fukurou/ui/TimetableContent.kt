package com.example.fukurou.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Report
import com.example.fukurou.data.Schoolday
import com.example.fukurou.dateformatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@ExperimentalFoundationApi
@Composable
fun TimetableContent(navController: NavHostController) {
    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxHeight()
    ) {
        for (item in DemoDataProvider.chunkSchooldays()) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .padding(32.dp, 0.dp, 0.dp, 0.dp)
                        .horizontalScroll(scrollState)
                ) {
                    TimetableHeader(DayOfWeek.MONDAY, item.monday?.date)
                    TimetableHeader(DayOfWeek.TUESDAY, item.tuesday?.date)
                    TimetableHeader(DayOfWeek.WEDNESDAY, item.wednesday?.date)
                    TimetableHeader(DayOfWeek.THURSDAY, item.thursday?.date)
                    TimetableHeader(DayOfWeek.FRIDAY, item.friday?.date)
                    TimetableHeader(DayOfWeek.SATURDAY, item.saturday?.date)
                    TimetableHeader(DayOfWeek.SUNDAY, item.sunday?.date)
                }
            }

            item {
                Row {
                    Column(modifier = Modifier.width(32.dp)) {

                    }

                    Column(
                        modifier = Modifier
                            .size(64.dp, 40.dp)
                            .padding(1.dp, 0.dp)
                    ) {
                        val schoolday = item.monday
                        if (schoolday != null) {
                            val lessons = DemoDataProvider.getLessons(schoolday)


                        }
                    }


                }
            }
        }
    }
}

@Composable
private fun TimetableHeader(week: DayOfWeek, date: LocalDate? = null, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(64.dp, 40.dp)
            .padding(1.dp, 0.dp)
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