package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.LoginRequest
import com.exam.me.model.RegisterRequest
import com.exam.me.network.ApiService
import kotlinx.coroutines.flow.first

// Simplified repository for authentication, only containing what's needed.
class AuthRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

    suspend fun register(request: RegisterRequest) = apiService.register(request)

    suspend fun login(request: LoginRequest) = apiService.login(request)

    // This is still needed for the Login/Register flow to get the user's role after getting a token.
    suspend fun getAuthUser(): com.exam.me.model.User {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) throw Exception("Not authenticated for getAuthUser call")
        return apiService.getAuthUser("Bearer $token")
    }
}