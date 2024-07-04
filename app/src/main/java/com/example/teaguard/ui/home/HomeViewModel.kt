package com.example.teaguard.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teaguard.data.local.entity.HistoryDiagnose
import com.example.teaguard.data.remote.response.DataDisease
import com.example.teaguard.data.remote.response.DiseaseByIdResponse
import com.example.teaguard.data.repository.DiseaseRepository
import com.example.teaguard.data.repository.HistoryDiagnoseRepository
import com.example.teaguard.foundation.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel (
    private val historyDiagnoseRepository: HistoryDiagnoseRepository,
    private val diseaseRepository: DiseaseRepository,
    application: Application
) : ViewModel() {

    private val _dataDisease = MutableSharedFlow<Result<DiseaseByIdResponse>>()
    val dataDisease : Flow<Result<DiseaseByIdResponse>> = _dataDisease.asSharedFlow()
    private val sharedPreferences = application.getSharedPreferences("DiagnosePrefs", Context.MODE_PRIVATE)
    fun getDiseaseById(id: String) {
        viewModelScope.launch {
            diseaseRepository.getDiseaseById(id).collect{
                _dataDisease.emit(it)
            }
        }
    }
    suspend fun saveDiagnose(historyDiagnose: HistoryDiagnose) {
        viewModelScope.launch {
            historyDiagnoseRepository.insert(historyDiagnose)
            saveToSharedPreferences(historyDiagnose)
        }
    }

    private fun saveToSharedPreferences(historyDiagnose: HistoryDiagnose) {
        val editor = sharedPreferences.edit()
        editor.putString("diagnosis_name", historyDiagnose.name)
        editor.putString("diagnosis_imageUri", historyDiagnose.imageUri)
        editor.putString("diagnosis_diagnosis", historyDiagnose.diagnosis)
        editor.putString("diagnosis_recommendation", historyDiagnose.recommendation)
        editor.putString("diagnosis_date", historyDiagnose.date)
        editor.apply()
    }

    fun getFromSharedPreferences(): HistoryDiagnose? {
        val name = sharedPreferences.getString("diagnosis_name", null) ?: return null
        val imageUri = sharedPreferences.getString("diagnosis_imageUri", null) ?: return null
        val diagnosis = sharedPreferences.getString("diagnosis_diagnosis", null) ?: return null
        val recommendation = sharedPreferences.getString("diagnosis_recommendation", null) ?: return null
        val date = sharedPreferences.getString("diagnosis_date", null) ?: return null

        return HistoryDiagnose(
            name = name,
            imageUri = imageUri,
            diagnosis = diagnosis,
            recommendation = recommendation,
            date = date
        )
    }
    fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}