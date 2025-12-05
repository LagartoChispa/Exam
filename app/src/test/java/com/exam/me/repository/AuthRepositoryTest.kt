package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.AuthResponse
import com.exam.me.model.LoginRequest
import com.exam.me.model.RegisterRequest
import com.exam.me.network.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        apiService = mockk()
        sessionManager = mockk()
        authRepository = AuthRepository(apiService)
    }

    @Test
    fun `register calls apiService and returns response`() = runBlocking {
        val request = RegisterRequest("Test User", "test@example.com", "password")
        val expectedResponse = AuthResponse("test_token")
        coEvery { apiService.register(request) } returns expectedResponse

        val result = authRepository.register(request)

        assert(result == expectedResponse)
        coVerify { apiService.register(request) }
    }

    @Test
    fun `login calls apiService and returns response`() = runBlocking {
        val request = LoginRequest("test@example.com", "password")
        val expectedResponse = AuthResponse("test_token")
        coEvery { apiService.login(request) } returns expectedResponse

        val result = authRepository.login(request)

        assert(result == expectedResponse)
        coVerify { apiService.login(request) }
    }
}