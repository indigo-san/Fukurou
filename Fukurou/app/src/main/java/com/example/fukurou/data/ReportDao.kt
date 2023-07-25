package com.example.fukurou.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ReportDao {
    @Insert
    suspend fun insert(item: Report)

    @Update
    suspend fun update(item: Report)

    @Delete
    suspend fun delete(item: Report)

    @Query("SELECT * from report WHERE id = :id")
    fun getItem(id: Int): Flow<Report>

    @Query("SELECT * from report WHERE date = :date")
    fun getItemByDate(date: LocalDate): Flow<List<Report>>

    @Query("SELECT * from report ORDER BY date ASC, subjectId ASC")
    fun getItems(): Flow<List<Report>>
}

