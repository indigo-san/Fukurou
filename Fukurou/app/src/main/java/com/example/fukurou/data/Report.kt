package com.example.fukurou.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "report")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val name: String,
    val subjectId: Int,
    val state: ReportState = ReportState.NotSubmitted,
    val isArchived: Boolean = false
) {
    @Ignore
    val isSubmitted: Boolean = state == ReportState.Submitted

    @Ignore
    val isNotSubmitted: Boolean = state == ReportState.NotSubmitted

    @Ignore
    val isExpired: Boolean = isNotSubmitted && date.isBefore(LocalDate.now())
}
