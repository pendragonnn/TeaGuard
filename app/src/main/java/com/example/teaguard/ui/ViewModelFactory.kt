package com.example.teaguard.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teaguard.data.repository.HistoryDiagnoseRepository
import com.example.teaguard.di.Injection
import com.example.teaguard.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val historyDiagnoseRepository: HistoryDiagnoseRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(historyDiagnoseRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val historyDiagnoseRepository = Injection.provideDiagnoseRepository(context)
                ViewModelFactory(historyDiagnoseRepository).also {
                    INSTANCE = it
                }
            }
        }
    }
}