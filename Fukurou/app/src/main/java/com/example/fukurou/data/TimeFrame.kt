package com.example.fukurou.data

import java.time.LocalDate
import java.time.LocalTime

data class TimeFrame(
    val number: Int,
    val start: LocalTime,
    val end: LocalTime
) {
    fun isCompoleted(date: LocalDate): Boolean {
        val now: LocalTime = LocalTime.now()
        val nowDate: LocalDate = LocalDate.now()

        return end < now && date <= nowDate
    }

    fun isDuring(date: LocalDate): Boolean {
        val now: LocalTime = LocalTime.now()
        val nowDate: LocalDate = LocalDate.now()
        return date == nowDate &&
                start < now &&
                now < end
    }
}
