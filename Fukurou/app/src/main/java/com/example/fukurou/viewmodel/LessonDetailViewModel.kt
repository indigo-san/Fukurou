package com.example.fukurou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fukurou.FukurouApplication
import com.example.fukurou.data.Lesson
import com.example.fukurou.data.LessonDao
import com.example.fukurou.data.Subject
import com.example.fukurou.data.SubjectDao
import com.example.fukurou.data.TimeFrame
import com.example.fukurou.data.TimeFrameDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class LessonDetailViewModel(
    private val lessonDao: LessonDao,
    private val subjectDao: SubjectDao,
    private val timeFrameDao: TimeFrameDao
) :
    ViewModel() {
    var recentlyUsedLesson: Lesson? = null

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val database = (this[APPLICATION_KEY] as FukurouApplication).database
                val lessonDao = database.lessonDao()
                val subjectDao = database.subjectDao()
                val timeFrameDao = database.timeFrameDao()
                LessonDetailViewModel(
                    lessonDao = lessonDao,
                    subjectDao = subjectDao,
                    timeFrameDao = timeFrameDao
                )
            }
        }
    }

    suspend fun canAddLesson(item: Lesson): Boolean {
        if (item.timeFrame < 1) {
            return false
        }

        val items = lessonDao.getItemsByDate(item.date).first()

        return !items.any { it.timeFrame == item.timeFrame }
    }

    fun addLesson(item: Lesson) {
        viewModelScope.launch {
            lessonDao.insert(item)
        }
    }

    fun getNewItemEntry(
        date: LocalDate,
        timeFrame: Int
    ): Lesson {
        return Lesson(date = date, timeFrame = timeFrame, subjectId = -1)
    }

    fun getSubject(id: Int): Flow<Subject?> {
        return subjectDao.getItem(id)
    }

    fun getSubjects(): Flow<List<Subject>> {
        return subjectDao.getItems()
    }

    suspend fun updateLesson(item: Lesson) {
        lessonDao.update(item)
    }

    fun getLessons(date: LocalDate): Flow<List<Lesson>> {
        return lessonDao.getItemsByDate(date)
    }

    fun getFrames(): Flow<List<TimeFrame>> {
        return timeFrameDao.getItems()
    }

    suspend fun deleteLesson(item: Lesson) {
        lessonDao.delete(item)
    }

    fun getLesson(id: Int): Flow<Lesson> {
        return lessonDao.getItem(id)
    }
}
