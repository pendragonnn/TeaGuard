package com.example.teaguard.ui.listDisease

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teaguard.data.remote.response.DiseaseDetailByIdResponse
import com.example.teaguard.data.remote.response.DiseaseDetailResponse
import com.example.teaguard.data.remote.response.DiseaseDetailResponseItem
import com.example.teaguard.data.repository.DiseaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.example.teaguard.foundation.utils.Result

class DiseaseViewModel(private val diseaseRepository: DiseaseRepository) : ViewModel() {

    private val _listDisease = MutableSharedFlow<Result<List<DiseaseDetailResponseItem>>>()
    val listDisease : Flow<Result<List<DiseaseDetailResponseItem>>> = _listDisease

    private val _listDiseaseById = MutableSharedFlow<Result<DiseaseDetailByIdResponse>>()
    val listDiseaseById : Flow<Result<DiseaseDetailByIdResponse>> = _listDiseaseById

    suspend fun  getAllDisease() {
        viewModelScope.launch {
            diseaseRepository.getAllDiseaseDetail().collect {
                _listDisease.emit(it)
            }
        }
    }

    suspend fun getDiseaseById(id: String) {
        viewModelScope.launch {
            diseaseRepository.getDiseaseDetailById(id).collect {
                _listDiseaseById.emit(it)
            }
        }
    }


}