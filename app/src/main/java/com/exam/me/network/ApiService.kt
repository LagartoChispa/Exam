package com.exam.me.network

import com.exam.me.model.*
import retrofit2.http.*

// This interface is now aligned with the backend documentation provided.
interface ApiService {

    // --- Auth Endpoints --- //

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("auth/profile")
    suspend fun getAuthUser(@Header("Authorization") token: String): User

    // --- User Profile Endpoints --- //

    @GET("usuario-profile/me")
    suspend fun getMyProfile(@Header("Authorization") token: String): UserProfile

    @PUT("usuario-profile/me")
    suspend fun updateMyProfile(@Header("Authorization") token: String, @Body profile: UserProfile): UserProfile

    // --- Movie (Pelicula) Endpoints --- //

    @GET("pelicula")
    suspend fun getMovies(@Header("Authorization") token: String): List<Movie>

    @GET("pelicula/{id}")
    suspend fun getMovieById(
        @Header("Authorization") token: String,
        @Path("id") movieId: String
    ): Movie

    // Note: Creating a movie and uploading an image are separate steps.
    // The app will focus on displaying movies for now.
}
