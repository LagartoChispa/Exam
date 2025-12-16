package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.ForgotPasswordRequest
import com.exam.me.model.LoginRequest
import com.exam.me.model.RegisterRequest
import com.exam.me.network.ApiService
import kotlinx.coroutines.flow.first

class AuthRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

    suspend fun register(request: RegisterRequest) = apiService.register(request)

    suspend fun login(request: LoginRequest) = apiService.login(request)

    suspend fun forgotPassword(request: ForgotPasswordRequest) = apiService.forgotPassword(request)

    suspend fun getAuthUser(): com.exam.me.model.User {
        val token = sessionManager.authToken.first()
        if (token.isNullOrEmpty()) throw Exception("Not authenticated for getAuthUser call")
        return apiService.getAuthUser("Bearer $token")
    }
}