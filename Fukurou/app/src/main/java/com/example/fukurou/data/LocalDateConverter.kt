package com.example.fukurou.data

import androidx.room.TypeConverter
import java.time.LocalDate

internal class LocalDateConverter {

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate): String {
        return localDate.toString()
    }

    @TypeConverter
    fun toLocalDate(stringDate: String): LocalDate {
        return LocalDate.parse(stringDate)
    }
}
