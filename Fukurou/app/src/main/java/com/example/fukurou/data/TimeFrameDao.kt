package com.example.fukurou.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeFrameDao {
    @Insert
    suspend fun insert(item: TimeFrame)

    @Update
    suspend fun update(item: TimeFrame)

    @Delete
    suspend fun delete(item: TimeFrame)

    @Query("SELECT * from timeFrame WHERE id = :id")
    fun getItem(id: Int): Flow<TimeFrame?>

    @Query("SELECT * from timeFrame ORDER BY number ASC")
    fun getItems(): Flow<List<TimeFrame>>

    @Query("SELECT * from timeFrame WHERE number = :num")
    fun getItemByNumber(num: Int): Flow<TimeFrame?>
}