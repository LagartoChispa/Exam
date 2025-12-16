package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.model.User
import com.exam.me.model.UserProfile
import com.exam.me.network.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody

class UserRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

    suspend fun getMyProfile(): UserProfile {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.getMyProfile("Bearer $token")
    }

    suspend fun updateMyProfile(profile: UserProfile): UserProfile {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.updateMyProfile("Bearer $token", profile)
    }

    suspend fun uploadProfileAvatar(image: MultipartBody.Part): UserProfile {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.uploadProfileAvatar("Bearer $token", image)
    }

    // --- Admin Methods ---

    suspend fun getAllUsers(): List<User> {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.getAllUsers("Bearer $token")
    }

    suspend fun createMovie(movie: Movie): Movie {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) {
            throw Exception("User not authenticated")
        }
        return apiService.createMovie("Bearer $token", movie)
    }
}