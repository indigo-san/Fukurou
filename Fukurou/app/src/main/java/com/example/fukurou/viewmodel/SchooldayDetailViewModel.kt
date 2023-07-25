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

class SchooldayDetailViewModel(
    private val lessonDao: LessonDao,
    private val subjectDao: SubjectDao,
    private val timeFrameDao: TimeFrameDao,
    private val reportDao: ReportDao,
) : ViewModel() {

    fun getLessons(date: LocalDate): Flow<List<Lesson>> {
        return lessonDao.getItemsByDate(date)
    }

    fun getSubject(id: Int): Flow<Subject?> {
        return subjectDao.getItem(id)
    }

    fun getTimeFrameOrNull(num: Int): Flow<TimeFrame?> {
        return timeFrameDao.getItemByNumber(num)
    }

    fun getReports(date: LocalDate): Flow<List<Report>> {
        return reportDao.getItemByDate(date)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val database =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FukurouApplication).database
                val lessonDao = database.lessonDao()
                val subjectDao = database.subjectDao()
                val timeFrameDao = database.timeFrameDao()
                val reportDao = database.reportDao()
                SchooldayDetailViewModel(
                    lessonDao = lessonDao,
                    subjectDao = subjectDao,
                    timeFrameDao = timeFrameDao,
                    reportDao = reportDao
                )
            }
        }
    }
}