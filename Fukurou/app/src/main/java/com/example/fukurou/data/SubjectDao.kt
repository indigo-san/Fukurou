package com.example.fukurou.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert
    suspend fun insert(item: Subject)

    @Update
    suspend fun update(item: Subject)

    @Delete
    suspend fun delete(item: Subject)

    @Query("SELECT * from subject WHERE id = :id")
    fun getItem(id: Int): Flow<Subject?>

    @Query("SELECT * from subject ORDER BY name ASC")
    fun getItems(): Flow<List<Subject>>

    @Query("SELECT * from subject ORDER BY name ASC LIMIT 1")
    fun firstOrNull(): Flow<Subject?>
}