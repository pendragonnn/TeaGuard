package com.example.teaguard.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teaguard.data.repository.DiseaseRepository
import com.example.teaguard.data.repository.HistoryDiagnoseRepository
import com.example.teaguard.di.Injection
import com.example.teaguard.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val historyDiagnoseRepository: HistoryDiagnoseRepository,
    private val diseaseRepository: DiseaseRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(historyDiagnoseRepository, diseaseRepository, application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val context = application.applicationContext
                val historyDiagnoseRepository = Injection.provideDiagnoseRepository(context)
                val diseaseRepository = Injection.provideDiseaseRepository(context)
                ViewModelFactory(historyDiagnoseRepository, diseaseRepository, application).also {
                    INSTANCE = it
                }
            }
        }
    }
}
