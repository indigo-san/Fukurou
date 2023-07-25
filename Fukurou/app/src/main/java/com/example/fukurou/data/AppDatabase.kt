package com.example.fukurou.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// スキーマを変更するとバージョンも変える必要がある
// https://developer.android.com/codelabs/basic-android-kotlin-training-persisting-data-room?hl=ja#6
// https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
@Database(entities = [
    Lesson::class,
    Report::class,
    Subject::class,
    TimeFrame::class],
    version = 1,
    //exportSchema = false
)
@TypeConverters(LocalDateConverter::class, LocalTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lessonDao(): LessonDao
    abstract fun reportDao(): ReportDao
    abstract fun subjectDao(): SubjectDao
    abstract fun timeFrameDao(): TimeFrameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    //.fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}