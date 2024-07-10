package com.example.teaguard.data.repository

import com.example.teaguard.data.remote.response.DiseaseByIdResponse
import com.example.teaguard.data.remote.response.DiseaseDetailByIdResponse
import com.example.teaguard.data.remote.response.DiseaseDetailResponse
import com.example.teaguard.data.remote.response.DiseaseDetailResponseItem
import com.example.teaguard.data.remote.retrofit.ApiService
import com.example.teaguard.foundation.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DiseaseRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun getDiseaseById(id: String): Flow<Result<DiseaseByIdResponse>> = flow {
        emit(Result.Loading)
        val response = apiService.getDiseaseById(id)
        emit(Result.Success(response))
    }.catch { e ->
        emit(Result.Error(e.message.toString()))
    }

    suspend fun getAllDiseaseDetail(): Flow<Result<List<DiseaseDetailResponseItem>>> = flow {
        emit(Result.Loading)
        val response = apiService.getAllDiseaseDetail()
        emit(Result.Success(response))
    }.catch { e ->
        emit(Result.Error(e.message.toString()))
    }

    suspend fun getDiseaseDetailById(id: String): Flow<Result<DiseaseDetailByIdResponse>> = flow {
        emit(Result.Loading)
        val response = apiService.getDiseaseDetailById(id)
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