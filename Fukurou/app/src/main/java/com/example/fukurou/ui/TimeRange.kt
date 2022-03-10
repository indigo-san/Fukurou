package com.example.fukurou.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fukurou.timeformatter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeRange(start: LocalTime, end: LocalTime) {
    Text(
        text = start.format(timeformatter),
        style = MaterialTheme.typography.bodyLarge
    )

    Text(
        "-",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(8.dp, 0.dp)
    )

    Text(
        text = end.format(timeformatter),
        style = MaterialTheme.typography.bodyMedium
    )
}
