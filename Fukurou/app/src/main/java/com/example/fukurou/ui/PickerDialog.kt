package com.example.fukurou.ui

import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

fun showDatePicker(date: LocalDate, activity: FragmentActivity, onClick: (LocalDate) -> Unit) {
    val calendar: Calendar = Calendar.getInstance()
    calendar.apply {
        set(Calendar.YEAR, date.year)
        set(Calendar.MONTH, date.monthValue - 1)
        set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
    }
    MaterialDatePicker.Builder.datePicker()
        .setSelection(calendar.timeInMillis).build().apply {
            show(activity.supportFragmentManager, "Tag")
            addOnPositiveButtonClickListener {
                val result: LocalDate =
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                onClick(result)
            }
        }
}

fun showTimePicker(time: LocalTime, activity: FragmentActivity, onClick: (LocalTime) -> Unit, title: String) {
    MaterialTimePicker.Builder()
        .setHour(time.hour)
        .setMinute(time.minute)
        .setTitleText(title)
        .build().apply {
            show(activity.supportFragmentManager, "Tag")
            addOnPositiveButtonClickListener {
                onClick(LocalTime.of(this.hour, this.minute))
            }
        }
}