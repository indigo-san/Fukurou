package com.example.fukurou.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface LessonDao {
    @Insert
    suspend fun insert(item: Lesson)

    @Update
    suspend fun update(item: Lesson)

    @Delete
    suspend fun delete(item: Lesson)

    @Query("SELECT * FROM lesson WHERE id = :id")
    fun getItem(id: Int): Flow<Lesson>

    @Query("SELECT * FROM lesson WHERE date = :date")
    fun getItemsByDate(date: LocalDate): Flow<List<Lesson>>

    @Query("SELECT * FROM lesson ORDER BY date ASC, timeFrame ASC")
    fun getItems(): Flow<List<Lesson>>

    @Query("SELECT date FROM lesson ORDER BY date DESC LIMIT 1")
    fun maxOfDate(): Flow<LocalDate?>

    @Query("""
SELECT
  T2.color
FROM
  lesson AS T1
  JOIN subject as T2
    ON T1.subjectId = T2.id
WHERE T1.date = :date
    """)
    fun dotColors(date: LocalDate): Flow<List<Long>>
}

