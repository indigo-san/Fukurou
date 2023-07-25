package com.example.fukurou.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "lesson")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val timeFrame: Int,
    val subjectId: Int,
    val state: LessonState = LessonState.None,
    val isArchived: Boolean = false,
    val tag: String = "",
    val room: String = ""
)

