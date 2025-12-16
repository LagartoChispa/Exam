package com.exam.me.ui.auth

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.exam.me.data.local.SessionManager
import com.exam.me.model.AuthResponse
import com.exam.me.model.RegisterRequest
import com.exam.me.model.User
import com.exam.me.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
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
class RegisterViewModelTest {

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

    private lateinit var registerViewModel: RegisterViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        registerViewModel = RegisterViewModel(application)
        val repositoryField = registerViewModel::class.java.getDeclaredField("authRepository")
        repositoryField.isAccessible = true
        repositoryField.set(registerViewModel, authRepository)

        val sessionManagerField = registerViewModel::class.java.getDeclaredField("sessionManager")
        sessionManagerField.isAccessible = true
        sessionManagerField.set(registerViewModel, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register with valid data updates state to success`() = runTest {
        val name = "Test User"
        val email = "test@test.com"
        val password = "password"
        val user = User("1", email, "USER", name, null)
        val authResponse = AuthResponse(user, "token")
        val registerRequest = RegisterRequest(name, email, password)

        coEvery { authRepository.register(registerRequest) } returns authResponse
        coEvery { sessionManager.saveSession(any(), any()) } returns Unit

        registerViewModel.onNameChange(name)
        registerViewModel.onEmailChange(email)
        registerViewModel.onPasswordChange(password)
        registerViewModel.register()

        val result = registerViewModel.registerResult.value
        assert(result is RegisterResult.Success)
        assertEquals(authResponse, (result as RegisterResult.Success).authResponse)
        coVerify { sessionManager.saveSession("token", "USER") }
    }

    @Test
    fun `register with invalid data updates state to error`() = runTest {
        val name = "Test User"
        val email = "test@test.com"
        val password = "password"
        val registerRequest = RegisterRequest(name, email, password)
        val errorMessage = "Email already exists"

        coEvery { authRepository.register(registerRequest) } throws Exception(errorMessage)

        registerViewModel.onNameChange(name)
        registerViewModel.onEmailChange(email)
        registerViewModel.onPasswordChange(password)
        registerViewModel.register()

        val result = registerViewModel.registerResult.value
        assert(result is RegisterResult.Error)
        assertEquals(errorMessage, (result as RegisterResult.Error).message)
    }

    @Test
    fun `register with invalid form does not call repository`() = runTest {
        registerViewModel.onNameChange("")
        registerViewModel.onEmailChange("invalid-email")
        registerViewModel.onPasswordChange("short")
        registerViewModel.register()

        coVerify(exactly = 0) { authRepository.register(any()) }
        assertNotNull(registerViewModel.formState.value.nombreError)
        assertNotNull(registerViewModel.formState.value.emailError)
        assertNotNull(registerViewModel.formState.value.passwordError)
    }
}
