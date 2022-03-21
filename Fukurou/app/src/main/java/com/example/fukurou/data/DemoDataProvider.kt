package com.example.fukurou.data

import java.lang.Integer.max
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields

data class Week(
    val monday: Schoolday?,
    val tuesday: Schoolday?,
    val wednesday: Schoolday?,
    val thursday: Schoolday?,
    val friday: Schoolday?,
    val saturday: Schoolday?,
    val sunday: Schoolday?
) {
    companion object {
        fun create(items: List<Schoolday?>): Week {
            var monday: Schoolday? = null
            var tuesday: Schoolday? = null
            var wednesday: Schoolday? = null
            var thursday: Schoolday? = null
            var friday: Schoolday? = null
            var saturday: Schoolday? = null
            var sunday: Schoolday? = null

            for (item in items) {
                if (item != null) {
                    when (item.date.dayOfWeek) {
                        DayOfWeek.MONDAY -> monday = item
                        DayOfWeek.TUESDAY -> tuesday = item
                        DayOfWeek.WEDNESDAY -> wednesday = item
                        DayOfWeek.THURSDAY -> thursday = item
                        DayOfWeek.FRIDAY -> friday = item
                        DayOfWeek.SATURDAY -> saturday = item
                        DayOfWeek.SUNDAY -> sunday = item
                        else -> {}
                    }
                }
            }

            return Week(
                monday, tuesday, wednesday, thursday, friday, saturday, sunday
            )
        }
    }
}

object DemoDataProvider {
    val subjects = mutableListOf(
        Subject("教科1", 0, 0xffff1744),
        Subject("教科2", 1, 0xff2962ff),
        Subject("教科3", 2, 0xff00c853),
        Subject("教科4", 3, 0xff00bfa5),
        Subject("教科5", 4, 0xffffab00),
    )

    val lessons = mutableListOf(
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

    val reports = mutableListOf(
        Report(
            name = "レポート1",
            date = LocalDate.now().plusDays(0),
            id = 0,
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

    val schooldays = mutableListOf(
        Schoolday(15, LocalDate.now().minusDays(6)),
        Schoolday(14, LocalDate.now().minusDays(5)),
        Schoolday(13, LocalDate.now().minusDays(4)),
//        Schoolday(12, LocalDate.now().minusDays(3)),
//        Schoolday(11, LocalDate.now().minusDays(2)),
//        Schoolday(10, LocalDate.now().minusDays(1)),
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

    fun getSchoolday(date: LocalDate): Schoolday {
        return schooldays.first { it.date == date }
    }

    fun getSchooldayOrNull(date: LocalDate): Schoolday? {
        return schooldays.firstOrNull { it.date == date }
    }

    fun createOrGetSchoolday(date: LocalDate): Schoolday {
        var id: Int = -1
        for (item in schooldays) {
            if (item.date == date) {
                return item
            }

            id = max(id, item.id)
        }
        id++

        val item = Schoolday(id, date)
        schooldays.add(item)
        schooldays.sortBy { it.date }
        return item
    }

    fun getSchoolday(id: Int): Schoolday {
        return schooldays.first { it.id == id }
    }

    fun deleteSchoolday(id: Int) {
        schooldays.removeIf { it.id == id }
    }

    fun chunkSchooldays(): List<Week> {
        val startSunday = false
        val field = if (startSunday)
            WeekFields.SUNDAY_START.dayOfWeek()
        else
            ChronoField.DAY_OF_WEEK

        val tmp: MutableList<Schoolday?> = mutableListOf()
        val result: MutableList<Week> = mutableListOf()

        for (index in 0..schooldays.size - 2) {
            val item = schooldays[index]
            val next = schooldays[index + 1]
            val itemValue = item.date.dayOfWeek.get(field)
            val nextValue = next.date.dayOfWeek.get(field)

            if (itemValue < nextValue) {
                tmp.add(item)
            } else if (itemValue > nextValue) {
                tmp.add(item)
                result.add(Week.create(tmp))
                tmp.clear()
            }
        }
        if (tmp.any()) {
            result.add(Week.create(tmp))
        }

        return result
    }

    fun getNextSchoolday(): Schoolday? {
        val now = LocalDate.now()
        return schooldays.firstOrNull { scd ->
            return@firstOrNull if (scd.date >= now) {
                val lessons = getLessons(scd)

                !lessons.any() || lessons.any { lesson -> !lesson.isCompoleted }
            } else {
                false
            }
        }
    }

    fun getSubject(id: Int): Subject {
        return subjects.first { it.id == id }
    }

    // レポート
    fun getReports(schoolday: Schoolday): List<Report> {
        return reports.filter { it.date == schoolday.date }
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
    fun getLessons(schoolday: Schoolday): List<Lesson> {
        val result = mutableListOf<Lesson>()
        lessons.filterTo(result) { it.date == schoolday.date }
        result.sortBy { it.start }
        return result
    }

    fun getLesson(id: Int): Lesson {
        return lessons.first { it.id == id }
    }

    fun updateLesson(lesson: Lesson) {
        val index = lessons.indexOfFirst { it.id == lesson.id }
        if (index >= 0) {
            lessons[index] = lesson
        }
    }

    fun deleteLesson(id: Int) {
        lessons.removeIf { it.id == id }
    }
}