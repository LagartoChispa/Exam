package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.ForgotPasswordRequest
import com.exam.me.model.LoginRequest
import com.exam.me.model.RegisterRequest
import com.exam.me.model.User
import com.exam.me.network.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var sessionManager: SessionManager

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        authRepository = AuthRepository(apiService, sessionManager)
    }

    @Test
    fun `register calls apiService register`() = runTest {
        val registerRequest = RegisterRequest("Test User", "test@test.com", "password")
        coEvery { apiService.register(registerRequest) } returns mockk()

        authRepository.register(registerRequest)

        coVerify { apiService.register(registerRequest) }
    }

    @Test
    fun `login calls apiService login`() = runTest {
        val loginRequest = LoginRequest("test@test.com", "password")
        coEvery { apiService.login(loginRequest) } returns mockk()

        authRepository.login(loginRequest)

        coVerify { apiService.login(loginRequest) }
    }

    @Test
    fun `forgotPassword calls apiService forgotPassword`() = runTest {
        val forgotPasswordRequest = ForgotPasswordRequest("test@test.com")
        coEvery { apiService.forgotPassword(forgotPasswordRequest) } returns Response.success(Unit)

        authRepository.forgotPassword(forgotPasswordRequest)

        coVerify { apiService.forgotPassword(forgotPasswordRequest) }
    }

    @Test
    fun `getAuthUser with valid token returns user`() = runTest {
        val token = "valid_token"
        val expectedUser = User("1", "test@test.com", "USER", "Test User", null)
        coEvery { sessionManager.authToken } returns flowOf(token)
        coEvery { apiService.getAuthUser("Bearer $token") } returns expectedUser

        val result = authRepository.getAuthUser()

        assertEquals(expectedUser, result)
        coVerify { apiService.getAuthUser("Bearer $token") }
    }

    @Test(expected = Exception::class)
    fun `getAuthUser without token throws exception`() = runTest {
        coEvery { sessionManager.authToken } returns flowOf(null)

        authRepository.getAuthUser()
    }
}
