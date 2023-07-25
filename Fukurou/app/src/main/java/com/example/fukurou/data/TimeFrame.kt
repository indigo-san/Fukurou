package com.example.fukurou.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "timeFrame")
data class TimeFrame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: Int,
    val start: LocalTime,
    val end: LocalTime
) {
    fun isCompleted(date: LocalDate): Boolean {
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
