package com.exam.me.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data models for the external API
data class Country(
    val name: CountryName,
    val capital: List<String>?,
    val population: Long,
    val flags: Flag
)

data class CountryName(
    val common: String,
    val official: String
)

data class Flag(
    val png: String,
    val svg: String
)

// Interface for the external API service
interface ExternalApiService {
    @GET("all")
    suspend fun getAllCountries(): List<Country>
}

// Retrofit instance for the external API
object ExternalApiInstance {
    private const val BASE_URL = "https://restcountries.com/v3.1/"

    val api: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}
