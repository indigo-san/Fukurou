package com.example.fukurou.data

import androidx.room.TypeConverter
import java.time.LocalTime

internal class LocalTimeConverter {

    @TypeConverter
    fun fromLocalTime(localTime: LocalTime): String {
        return localTime.toString()
    }

    @TypeConverter
    fun toLocalTime(stringTime: String): LocalTime {
        return LocalTime.parse(stringTime)
    }
}