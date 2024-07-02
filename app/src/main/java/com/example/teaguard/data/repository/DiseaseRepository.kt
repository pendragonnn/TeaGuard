package com.example.teaguard.data.repository

import android.provider.ContactsContract.Data
import com.example.teaguard.data.local.room.HistoryDiagnoseDao
import com.example.teaguard.data.remote.response.DataItem
import com.example.teaguard.data.remote.retrofit.ApiConfig
import com.example.teaguard.data.remote.retrofit.ApiService
import com.example.teaguard.foundation.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DiseaseRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun getDiseaseById(id: String): Flow<Result<DataItem>> = flow {
        emit(Result.Loading)
        val response = apiService.getDiseaseById(id)
        emit(Result.Success(response))
    }.catch { e ->
        emit(Result.Error(e.message.toString()))
    }
    companion object {
        @Volatile
        private var instance: DiseaseRepository? = null

        fun getInstance(apiService: ApiService): DiseaseRepository =
            instance ?: synchronized(this) {
                instance ?: DiseaseRepository(apiService).also { instance = it }
            }
    }
}