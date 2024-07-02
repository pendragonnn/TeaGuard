package com.example.teaguard.di

import android.content.Context
import com.example.teaguard.data.local.room.HistoryDatabase
import com.example.teaguard.data.remote.retrofit.ApiConfig
import com.example.teaguard.data.repository.DiseaseRepository
import com.example.teaguard.data.repository.HistoryDiagnoseRepository

object Injection {
    fun provideDiagnoseRepository(context : Context) : HistoryDiagnoseRepository {
        val database = HistoryDatabase.getDatabase(context)
        val dao = database.historyDao()

        return HistoryDiagnoseRepository.getInstance(dao)
    }
    fun provideDiseaseRepository(context : Context) : DiseaseRepository {
        val apiService = ApiConfig.getApiService()

        return DiseaseRepository.getInstance(apiService)
    }
}