package com.exam.me.network

import com.exam.me.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Auth Endpoints --- //

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @GET("auth/profile")
    suspend fun getAuthUser(@Header("Authorization") token: String): User

    // --- User Profile Endpoints --- //

    @GET("usuario-profile/me")
    suspend fun getMyProfile(@Header("Authorization") token: String): UserProfile

    @PUT("usuario-profile/me")
    suspend fun updateMyProfile(@Header("Authorization") token: String, @Body profile: UserProfile): UserProfile

    @Multipart
    @POST("usuario-profile/me/avatar")
    suspend fun uploadProfileAvatar(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): UserProfile

    // --- Admin Endpoints --- //
    @GET("usuario")
    suspend fun getAllUsers(@Header("Authorization") token: String): List<User>

    @POST("pelicula")
    suspend fun createMovie(@Header("Authorization") token: String, @Body movie: Movie): Movie

    // --- Movie (Pelicula) Endpoints --- //

    @GET("pelicula")
    suspend fun getMovies(@Header("Authorization") token: String): List<Movie>

    @GET("pelicula/{id}")
    suspend fun getMovieById(
        @Header("Authorization") token: String,
        @Path("id") movieId: String
    ): Movie

}
