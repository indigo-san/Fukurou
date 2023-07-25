package com.example.fukurou.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fukurou.FukurouApplication
import com.example.fukurou.data.Report
import com.example.fukurou.data.ReportDao
import com.example.fukurou.data.Subject
import com.example.fukurou.data.SubjectDao
import kotlinx.coroutines.flow.Flow

class ReportDetailViewModel(
    private val subjectDao: SubjectDao,
    private val reportDao: ReportDao
) : ViewModel() {

    fun getSubject(subjectId: Int): Flow<Subject?> {
        return subjectDao.getItem(subjectId)
    }

    suspend fun updateReport(item: Report) {
        reportDao.update(item)
    }

    suspend fun deleteReport(item: Report) {
        reportDao.delete(item)
    }

    fun getSubjects(): Flow<List<Subject>> {
        return subjectDao.getItems()
    }

    fun getReport(id: Int): Flow<Report?> {
        return reportDao.getItem(id)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val database =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FukurouApplication).database
                val subjectDao = database.subjectDao()
                val reportDao = database.reportDao()
                ReportDetailViewModel(
                    subjectDao = subjectDao,
                    reportDao = reportDao
                )
            }
        }
    }
}
