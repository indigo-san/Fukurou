package com.example.fukurou.data

import android.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

object DemoDataProvider {
    val subjects = listOf(
        Subject("教科1", 0, 0xffff1744),
        Subject("教科2", 1, 0xff2962ff),
        Subject("教科3", 2, 0xff00c853),
        Subject("教科4", 3, 0xff00bfa5),
        Subject("教科5", 4, 0xffffab00),
    )

    val lessons = listOf(
        Lesson(
            date = LocalDate.now().plusDays(0),
            start = LocalTime.of(9, 0),
            end = LocalTime.of(9, 50),
            id = 0,
            subjectId = 0
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(10, 50),
            id = 1,
            subjectId = 1
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(10, 50),
            id = 2,
            subjectId = 2
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(10, 50),
            id = 3,
            subjectId = 3
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(10, 50),
            id = 4,
            subjectId = 4
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            start = LocalTime.of(9, 0),
            end = LocalTime.of(9, 50),
            id = 5,
            subjectId = 0
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(10, 50),
            id = 6,
            subjectId = 1
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            start = LocalTime.of(11, 0),
            end = LocalTime.of(11, 50),
            id = 7,
            subjectId = 2
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            start = LocalTime.of(12, 0),
            end = LocalTime.of(12, 50),
            id = 8,
            subjectId = 3
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(13, 50),
            id = 9,
            subjectId = 4
        ),
    )

    val schooldays = listOf(
        Schoolday(0, LocalDate.now().plusDays(0)),
        Schoolday(1, LocalDate.now().plusDays(1)),
        Schoolday(2, LocalDate.now().plusDays(2)),
        Schoolday(3, LocalDate.now().plusDays(3)),
        Schoolday(4, LocalDate.now().plusDays(4)),
        Schoolday(5, LocalDate.now().plusDays(5)),
        Schoolday(6, LocalDate.now().plusDays(6)),
        Schoolday(7, LocalDate.now().plusDays(7)),
        Schoolday(8, LocalDate.now().plusDays(8)),
        Schoolday(9, LocalDate.now().plusDays(9)),
    )

    fun getLessons(schoolday: Schoolday): List<Lesson> {
        return lessons.filter { it.date == schoolday.date }
    }

    fun getSchoolday(date: LocalDate): Schoolday {
        return schooldays.first { it.date == date }
    }

    fun getSchoolday(id: Int): Schoolday {
        return schooldays.first { it.id == id }
    }

    fun getNextSchoolday(): Schoolday? {
        val now = LocalDate.now()
        return schooldays.firstOrNull { scd ->
            scd.date >= now && getLessons(scd).any { lesson -> !lesson.isCompoleted }
        }
    }

    fun getSubject(id: Int): Subject {
        return subjects.first { it.id == id }
    }
}