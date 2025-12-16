package com.exam.me.ui.auth

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.exam.me.data.local.SessionManager
import com.exam.me.model.AuthResponse
import com.exam.me.model.LoginRequest
import com.exam.me.model.User
import com.exam.me.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var sessionManager: SessionManager

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var loginViewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // We need to mock the constructor of AuthRepository as it's created inside the ViewModel
        loginViewModel = LoginViewModel(application)
        // Inject the mocked repository
        val repositoryField = loginViewModel::class.java.getDeclaredField("authRepository")
        repositoryField.isAccessible = true
        repositoryField.set(loginViewModel, authRepository)

        val sessionManagerField = loginViewModel::class.java.getDeclaredField("sessionManager")
        sessionManagerField.isAccessible = true
        sessionManagerField.set(loginViewModel, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials updates state to success`() = runTest {
        val email = "test@test.com"
        val password = "password"
        val user = User("1", email, "USER", "Test", null)
        val authResponse = AuthResponse(user, "token")
        val loginRequest = LoginRequest(email, password)

        coEvery { authRepository.login(loginRequest) } returns authResponse
        coEvery { sessionManager.saveSession(any(), any()) } returns Unit

        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        loginViewModel.login()

        val result = loginViewModel.loginResult.value
        assert(result is LoginResult.Success)
        assertEquals(authResponse, (result as LoginResult.Success).authResponse)
        coVerify { sessionManager.saveSession("token", "USER") }
    }

    @Test
    fun `login with invalid credentials updates state to error`() = runTest {
        val email = "test@test.com"
        val password = "wrongpassword"
        val loginRequest = LoginRequest(email, password)
        val errorMessage = "Invalid credentials"

        coEvery { authRepository.login(loginRequest) } throws Exception(errorMessage)

        loginViewModel.onEmailChange(email)
        loginViewModel.onPasswordChange(password)
        loginViewModel.login()

        val result = loginViewModel.loginResult.value
        assert(result is LoginResult.Error)
        assertEquals(errorMessage, (result as LoginResult.Error).message)
    }

    @Test
    fun `login with invalid form does not call repository`() = runTest {
        loginViewModel.onEmailChange("invalid-email")
        loginViewModel.onPasswordChange("")
        loginViewModel.login()

        coVerify(exactly = 0) { authRepository.login(any()) }
        assertNotNull(loginViewModel.formState.value.emailError)
        assertNotNull(loginViewModel.formState.value.passwordError)
    }
}
