package com.exam.me.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// TMDb API Key - Replace with your actual key
private const val TMDB_API_KEY = "YOUR_TMDB_API_KEY"

// --- TMDb Data Models ---

data class TmdbMovieSearchResult(
    val results: List<TmdbMovie>
)

data class TmdbMovie(
    val id: Int,
    @SerializedName("poster_path")
    val posterPath: String?
)

// --- TMDb API Service ---

interface ExternalApiService {
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") apiKey: String = TMDB_API_KEY,
        @Query("query") movieTitle: String
    ): TmdbMovieSearchResult
}

// --- Retrofit Instance for TMDb ---

object ExternalApiInstance {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val api: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}