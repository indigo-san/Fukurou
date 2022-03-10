package com.example.fukurou.ui.details

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
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
            Text("Hello",
                Modifier
                    .fillMaxWidth()
                    .height(200.dp))
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun Section(
    title: @Composable () -> Unit = {},
    onClick: () -> Unit,
    icon: ImageVector,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
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