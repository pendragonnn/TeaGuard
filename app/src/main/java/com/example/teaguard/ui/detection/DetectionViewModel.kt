package com.example.teaguard.ui.detection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teaguard.data.local.entity.HistoryDiagnose
import com.example.teaguard.data.repository.HistoryDiagnoseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DetectionViewModel (private val historyDiagnoseRepository: HistoryDiagnoseRepository) : ViewModel() {

    private val _detectionList = MutableSharedFlow<List<HistoryDiagnose>>()
    private val detectionList : Flow<List<HistoryDiagnose>> = _detectionList.asSharedFlow()

    init {
        getDetectionList()
    }

    private fun getDetectionList() {
        viewModelScope.launch {
            historyDiagnoseRepository.getAllHistory().collect {
                _detectionList.emit(it)
            }
        }
    }
}