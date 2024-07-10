package com.example.teaguard.data.remote.retrofit


import com.example.teaguard.data.remote.response.DiseaseByIdResponse
import com.example.teaguard.data.remote.response.DiseaseDetailByIdResponse
import com.example.teaguard.data.remote.response.DiseaseDetailResponse
import com.example.teaguard.data.remote.response.DiseaseDetailResponseItem
import com.example.teaguard.data.remote.response.DiseaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("v1/disease")
    suspend fun getAllDisease() : DiseaseResponse

    @GET("v1/disease/{id}")
    suspend fun getDiseaseById(@Path("id") id: String) : DiseaseByIdResponse

    @GET("v1/disease_details")
    suspend fun getAllDiseaseDetail() : List<DiseaseDetailResponseItem>

    @GET("v1/disease_details/{id}")
    suspend fun getDiseaseDetailById(@Path("id") id: String) : DiseaseDetailByIdResponse
}