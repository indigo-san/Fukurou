package com.example.fukurou.data

import java.time.LocalDate
import java.time.LocalTime

data class Lesson(
    val date: LocalDate,
    val timeFrame: Int,
    val id: Int,
    val subjectId: Int,
    val state: LessonState = LessonState.None,
    val isArchived: Boolean = false,
    val tag: String = "",
    val room: String = ""
)