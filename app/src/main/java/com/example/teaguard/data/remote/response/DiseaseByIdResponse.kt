package com.example.teaguard.data.remote.response

import com.google.gson.annotations.SerializedName

data class DiseaseByIdResponse(

	@field:SerializedName("data")
	val data: DataDisease? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DataDisease(

	@field:SerializedName("diseaseName")
	val diseaseName: String? = null,

	@field:SerializedName("diseaseRecommendation")
	val diseaseRecommendation: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("diseaseExplanation")
	val diseaseExplanation: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
