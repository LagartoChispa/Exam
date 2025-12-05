package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.ApiService
import kotlinx.coroutines.flow.first

class MovieRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

    suspend fun getMovies(): List<Movie> {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.getMovies("Bearer $token")
    }

    suspend fun getMovieById(movieId: String): Movie {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.getMovieById("Bearer $token", movieId)
    }
}