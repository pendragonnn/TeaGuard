package com.example.teaguard.data.remote.retrofit

import com.example.teaguard.data.remote.response.DataDisease
import com.example.teaguard.data.remote.response.DataItem
import com.example.teaguard.data.remote.response.DiseaseByIdResponse
import com.example.teaguard.data.remote.response.DiseaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("v1/disease")
    suspend fun getAllDisease() : DiseaseResponse

    @GET("v1/disease/{id}")
    suspend fun getDiseaseById(@Path("id") id: String) : DiseaseByIdResponse

}