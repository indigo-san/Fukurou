package com.example.fukurou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fukurou.FukurouApplication
import com.example.fukurou.data.Lesson
import com.example.fukurou.data.LessonDao
import com.example.fukurou.data.Report
import com.example.fukurou.data.ReportDao
import com.example.fukurou.data.Subject
import com.example.fukurou.data.SubjectDao
import com.example.fukurou.data.TimeFrame
import com.example.fukurou.data.TimeFrameDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TimetableContentViewModel(
    private val subjectDao: SubjectDao,
    private val lessonDao: LessonDao,
    private val reportDao: ReportDao,
    private val timeFrameDao: TimeFrameDao
) : ViewModel() {

    fun getSubject(subjectId: Int): Flow<Subject?> {
        return subjectDao.getItem(subjectId)
    }

    fun getTimeFrames(): Flow<List<TimeFrame>> {
        return timeFrameDao.getItems()
    }

    fun getLessons(date: LocalDate): Flow<List<Lesson>> {
        return lessonDao.getItemsByDate(date)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val database =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FukurouApplication).database
                val subjectDao = database.subjectDao()
                val lessonDao = database.lessonDao()
                val reportDao = database.reportDao()
                val timeFrameDao = database.timeFrameDao()
                TimetableContentViewModel(
                    subjectDao = subjectDao,
                    lessonDao = lessonDao,
                    reportDao = reportDao,
                    timeFrameDao = timeFrameDao
                )
            }
        }
    }
}