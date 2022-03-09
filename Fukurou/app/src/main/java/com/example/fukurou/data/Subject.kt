package com.example.fukurou.data

data class Subject(
    val name: String,
    val id: Int,
    val color: Long,
    val requiredAttendance: Int = -1
)