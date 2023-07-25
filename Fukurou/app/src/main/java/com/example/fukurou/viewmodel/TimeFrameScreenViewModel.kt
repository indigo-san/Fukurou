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
import com.example.fukurou.data.TimeFrame
import com.example.fukurou.data.TimeFrameDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime

enum class TimeFrameScreenStatus {
    None,
    Add,
    Edit
}

class TimeFrameScreenViewModel(
    private val lessonDao: LessonDao,
    private val subjectDao: SubjectDao,
    private val timeFrameDao: TimeFrameDao,
    private val reportDao: ReportDao,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    private val _frames = MutableStateFlow<MutableList<TimeFrame>>(mutableListOf())
    private val _number = MutableStateFlow(1)
    private val _startTime = MutableStateFlow(LocalTime.MIN)
    private val _endTime = MutableStateFlow(LocalTime.MIN)
    private val _status = MutableStateFlow(TimeFrameScreenStatus.None)
    private var _id: Int = 0

    val isRefreshing = _isRefreshing.asStateFlow()
    val frames: StateFlow<List<TimeFrame>> get() = _frames
    val number = _number.asStateFlow()
    val startTime = _startTime.asStateFlow()
    val endTime = _endTime.asStateFlow()
    val status = _status.asStateFlow()


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

    fun setStartTime(time: LocalTime) {
        _startTime.value = time
    }

    fun setEndTime(time: LocalTime) {
        _endTime.value = time
    }

    fun setNumber(num: Int) {
        _number.value = num
    }

    fun startAdd() {
        _startTime.value = LocalTime.now()
        _endTime.value = LocalTime.now()
        _status.value = TimeFrameScreenStatus.Add
        _number.value = _frames.value.size
    }

    fun endAdd() {
        viewModelScope.launch {
            val number = number.value
            val exist = _frames.value.find { it.number == number }

            if (exist != null) {
                timeFrameDao.update(
                    TimeFrame(
                        id = exist.id,
                        number = number,
                        start = startTime.value,
                        end = endTime.value
                    )
                )
            } else {
                timeFrameDao.insert(
                    TimeFrame(
                        number = number,
                        start = startTime.value,
                        end = endTime.value
                    )
                )
            }

            refreshCore()
        }
    }

    fun startEdit(item: TimeFrame) {
        _id = item.id
        _startTime.value = item.start
        _endTime.value = item.end
        _status.value = TimeFrameScreenStatus.Edit
        _number.value = item.number
    }

    fun endEdit() {
        viewModelScope.launch {
            timeFrameDao.update(
                TimeFrame(
                    id = _id,
                    number = number.value,
                    start = startTime.value,
                    end = endTime.value
                )
            )

            refreshCore()
        }
    }

    private suspend fun refreshCore() {
        _frames.value.clear()
        _frames.emit(timeFrameDao.getItems().first().toMutableList())
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
                TimeFrameScreenViewModel(
                    lessonDao = lessonDao,
                    subjectDao = subjectDao,
                    timeFrameDao = timeFrameDao,
                    reportDao = reportDao
                )
            }
        }
    }
}