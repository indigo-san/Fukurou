package com.example.fukurou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fukurou.FukurouApplication
import com.example.fukurou.data.LessonDao
import com.example.fukurou.data.ReportDao
import com.example.fukurou.data.SubjectDao
import com.example.fukurou.data.TimeFrameDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(
    private val lessonDao: LessonDao,
    private val subjectDao: SubjectDao,
    private val timeFrameDao: TimeFrameDao,
    private val reportDao: ReportDao,
) : ViewModel() {
    private val _nextDay = MutableStateFlow<LocalDate?>(null)
    val nextDay = _nextDay.asStateFlow()

    private val _dotColors = MutableStateFlow<List<Long>>(emptyList())
    val dotColors = _dotColors.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                refreshCore()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun refreshCore() {
        val nextDay = getNextSchoolday()
        if (nextDay != null) {
            _dotColors.value = lessonDao.dotColors(nextDay).first()
        }

        _nextDay.value = nextDay
    }

    private suspend fun getNextSchoolday(): LocalDate? {
        try {
            var now = LocalDate.now()
            val max = now.plusDays(365)

            while (now <= max) {
                val items = lessonDao.getItemsByDate(now).first()
                if (items.any { lesson ->
                        val frame = timeFrameDao.getItemByNumber(lesson.timeFrame).first()
                        frame?.isCompleted(lesson.date) != true
                    }
                ) {
                    return now
                } else {
                    now = now.plusDays(1)
                }
            }

            return null
        } catch (e: Exception) {
            println(e.message)
            return null
        }
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
                DashboardViewModel(
                    lessonDao = lessonDao,
                    subjectDao = subjectDao,
                    timeFrameDao = timeFrameDao,
                    reportDao = reportDao
                )
            }
        }
    }
}

