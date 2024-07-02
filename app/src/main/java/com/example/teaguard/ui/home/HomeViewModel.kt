package com.example.teaguard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teaguard.data.remote.response.DataItem
import com.example.teaguard.data.repository.HistoryDiagnoseRepository
import com.example.teaguard.foundation.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel (private val historyDiagnoseRepository: HistoryDiagnoseRepository) : ViewModel() {

    private val _dataDisease = MutableSharedFlow<Result<DataItem>>()
    val dataDisease : Flow<Result<DataItem>> = _dataDisease.asSharedFlow()

    fun getDisease(id: String) {
        viewModelScope.launch {
            historyDiagnoseRepository
        }
    }
}