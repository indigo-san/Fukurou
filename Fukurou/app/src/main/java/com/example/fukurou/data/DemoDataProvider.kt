package com.example.fukurou.data

import java.lang.Integer.max
import java.time.LocalDate
import java.time.LocalTime

object DemoDataProvider {
    val timeFrames = mutableListOf(
        TimeFrame(1, 1, LocalTime.of(9, 0), LocalTime.of(9, 45)),
        TimeFrame(2, 2, LocalTime.of(9, 55), LocalTime.of(10, 40)),
        TimeFrame(3, 3, LocalTime.of(10, 50), LocalTime.of(11, 35)),
        TimeFrame(4, 4, LocalTime.of(11, 45), LocalTime.of(12, 30)),
        TimeFrame(5, 5, LocalTime.of(13, 35), LocalTime.of(14, 20)),
        TimeFrame(6, 6, LocalTime.of(14, 30), LocalTime.of(15, 15)),
        TimeFrame(7, 7, LocalTime.of(15, 25), LocalTime.of(16, 10)),
    )

    val subjects = mutableListOf(
        Subject(1, "教科1", 0xffff1744),
        Subject(2, "教科2", 0xff2962ff),
        Subject(3, "教科3", 0xff00c853),
        Subject(4, "教科4", 0xff00bfa5),
        Subject(5, "教科5", 0xffffab00),
    )

    val lessons = mutableListOf(
        Lesson(
            date = LocalDate.now().plusDays(0),
            timeFrame = 1,
            id = 10,
            subjectId = 0
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            timeFrame = 2,
            id = 1,
            subjectId = 1
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            timeFrame = 3,
            id = 2,
            subjectId = 2
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            timeFrame = 4,
            id = 3,
            subjectId = 3
        ),
        Lesson(
            date = LocalDate.now().plusDays(0),
            timeFrame = 5,
            id = 4,
            subjectId = 4
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            timeFrame = 1,
            id = 5,
            subjectId = 0
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            timeFrame = 2,
            id = 6,
            subjectId = 1
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            timeFrame = 3,
            id = 7,
            subjectId = 2
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            timeFrame = 4,
            id = 8,
            subjectId = 3
        ),
        Lesson(
            date = LocalDate.now().plusDays(1),
            timeFrame = 5,
            id = 9,
            subjectId = 4
        ),
    )

    val reports = mutableListOf(
        Report(
            name = "レポート1",
            date = LocalDate.now().plusDays(0),
            id = 17,
            subjectId = 0
        ),
        Report(
            name = "レポート2",
            date = LocalDate.now().plusDays(0),
            id = 2,
            subjectId = 1
        ),
        Report(
            name = "レポート3",
            date = LocalDate.now().plusDays(0),
            id = 3,
            subjectId = 2
        ),
        Report(
            name = "レポート4",
            date = LocalDate.now().plusDays(0),
            id = 4,
            subjectId = 3
        ),
        Report(
            name = "レポート5",
            date = LocalDate.now().plusDays(1),
            id = 5,
            subjectId = 0
        ),
        Report(
            name = "レポート6",
            date = LocalDate.now().plusDays(1),
            id = 6,
            subjectId = 1
        ),
        Report(
            name = "レポート7",
            date = LocalDate.now().plusDays(1),
            id = 7,
            subjectId = 2
        ),
        Report(
            name = "レポート8",
            date = LocalDate.now().plusDays(1),
            id = 8,
            subjectId = 3
        ),
        Report(
            name = "レポート9",
            date = LocalDate.now().plusDays(2),
            id = 9,
            subjectId = 0
        ),
        Report(
            name = "レポート10",
            date = LocalDate.now().plusDays(2),
            id = 10,
            subjectId = 1
        ),
        Report(
            name = "レポート11",
            date = LocalDate.now().plusDays(2),
            id = 11,
            subjectId = 2
        ),
        Report(
            name = "レポート12",
            date = LocalDate.now().plusDays(2),
            id = 12,
            subjectId = 3
        ),
        Report(
            name = "レポート13",
            date = LocalDate.now().plusDays(3),
            id = 13,
            subjectId = 0
        ),
        Report(
            name = "レポート14",
            date = LocalDate.now().plusDays(3),
            id = 14,
            subjectId = 1
        ),
        Report(
            name = "レポート15",
            date = LocalDate.now().plusDays(3),
            id = 15,
            subjectId = 2
        ),
        Report(
            name = "レポート16",
            date = LocalDate.now().plusDays(3),
            id = 16,
            subjectId = 3
        )
    )

    var recentlyUsedLesson: Lesson? = null

    fun getNextSchoolday(): LocalDate? {
        var now = LocalDate.now()
        val max = lessons.maxOf { it.date }

        while (now <= max) {
            val items = lessons.filter { it.date == now }
            if (!items.any() || items.any { lesson ->
                    getTimeFrameOrNull(lesson.timeFrame)?.isCompleted(lesson.date) != true
                }
            ) {
                return now
            } else {
                now = now.plusDays(1)
            }
        }

        return null
    }

    fun getSubject(id: Int): Subject {
        return subjects.first { it.id == id }
    }

    fun getSubjectOrNull(id: Int): Subject? {
        return subjects.firstOrNull { it.id == id }
    }

    // レポート
    fun getReports(date: LocalDate): List<Report> {
        return reports.filter { it.date == date }
    }

    fun getReport(id: Int): Report {
        return reports.first { it.id == id }
    }

    fun updateReport(report: Report) {
        val index = reports.indexOfFirst { it.id == report.id }
        if (index >= 0) {
            reports[index] = report
        }
    }

    fun deleteReport(id: Int) {
        reports.removeIf { it.id == id }
    }

    // 授業
    fun getLessons(date: LocalDate): List<Lesson> {
        val result = mutableListOf<Lesson>()
        lessons.filterTo(result) { it.date == date }
        result.sortBy { it.timeFrame }
        return result
    }

    fun getLesson(id: Int): Lesson {
        return lessons.first { it.id == id }
    }

    fun updateLesson(lesson: Lesson) {
        val index = lessons.indexOfFirst { it.id == lesson.id }
        if (index >= 0) {
            val near = lessons.indexOfFirst {
                it.date == lesson.date && it.timeFrame == lesson.timeFrame
            }
            if (near != -1) {
                lessons[near] = lessons[near].copy(timeFrame = lessons[index].timeFrame)
            }

            lessons[index] = lesson
        }
    }

    fun deleteLesson(id: Int) {
        lessons.removeIf { it.id == id }
    }

    fun getNextLessonId(): Int {
        var id: Int = -1
        for (item in lessons) {
            id = max(id, item.id)
        }
        return ++id
    }

    fun addLesson(lesson: Lesson) {
        lessons.add(lesson)
    }

    fun canAddLesson(lesson: Lesson): Boolean {
        if (lesson.timeFrame < 1) {
            return false
        }

        val items = lessons.filter { it.date == lesson.date }

        return !items.any { it.timeFrame == lesson.timeFrame }
    }

    // timeframe
    fun getTimeFrameOrNull(num: Int): TimeFrame? {
        return timeFrames.firstOrNull { it.number == num }
    }
}