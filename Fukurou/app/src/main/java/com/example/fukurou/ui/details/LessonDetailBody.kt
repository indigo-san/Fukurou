package com.example.fukurou.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fukurou.data.DemoDataProvider
import com.example.fukurou.data.Lesson
import com.example.fukurou.ui.CardScaffold

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun LessonDetailBodyPreview() {
    LessonDetailBody(lesson = DemoDataProvider.lessons[0])
}

@ExperimentalMaterial3Api
@Composable
fun LessonDetailBody(lesson: Lesson) {
    val subject = DemoDataProvider.getSubject(lesson.subjectId)
    Column {
        CardScaffold(
            title = { Text("教科") }
        ) {
            Text(subject.name)
        }
    }
}