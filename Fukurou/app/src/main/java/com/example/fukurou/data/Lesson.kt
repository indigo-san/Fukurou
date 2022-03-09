package com.example.fukurou.data

import java.time.LocalDate
import java.time.LocalTime

data class Lesson(
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    val id: Int,
    val subjectId: Int,
    val state: LessonState = LessonState.None,
    val isArchived: Boolean = false,
    val tag: String = "",
    val room: String = ""
) {
    val isCompoleted: Boolean
        get() {
            val now: LocalTime = LocalTime.now()
            val nowDate: LocalDate = LocalDate.now()
            return end < now && date <= nowDate;
        }

    val isDuring: Boolean
        get() {
            val now: LocalTime = LocalTime.now()
            val nowDate: LocalDate = LocalDate.now()
            return date == nowDate &&
                    start < now &&
                    now < end;
        }
}