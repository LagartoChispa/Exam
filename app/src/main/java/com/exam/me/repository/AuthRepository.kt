package com.exam.me.repository

import com.exam.me.model.RegisterRequest
import com.exam.me.network.ApiService

class AuthRepository(private val apiService: ApiService) {
    suspend fun register(request: RegisterRequest) = apiService.register(request)
}