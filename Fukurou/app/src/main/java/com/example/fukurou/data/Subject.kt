package com.example.fukurou.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Long,
    val requiredAttendance: Int = -1
)