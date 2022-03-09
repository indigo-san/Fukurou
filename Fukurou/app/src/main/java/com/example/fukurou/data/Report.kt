package com.example.fukurou.data

import java.time.LocalDate

data class Report(
    val id: Int,
    val date: LocalDate,
    val name: String,
    val state: ReportState = ReportState.NotSubmitted,
    val isArchived: Boolean = false
) {
    val isSubmitted: Boolean = state == ReportState.Submitted

    val isNotSubmitted: Boolean = state == ReportState.NotSubmitted

    val isExpired: Boolean = isNotSubmitted && date.isBefore(LocalDate.now())
}
