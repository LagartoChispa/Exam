package com.exam.me.ui.auth

import android.app.Application
import com.exam.me.data.local.SessionManager
import com.exam.me.model.AuthResponse
import com.exam.me.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: RegisterViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var application: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        authRepository = mockk()
        sessionManager = mockk(relaxed = true)
        // This is a workaround to inject mocks into the ViewModel
        viewModel = object : RegisterViewModel(application) {
            init {
                this.javaClass.getDeclaredField("authRepository").apply {
                    isAccessible = true
                    set(this@object, authRepository)
                }
                this.javaClass.getDeclaredField("sessionManager").apply {
                    isAccessible = true
                    set(this@object, sessionManager)
                }
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success updates state and saves token`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val name = "Test User"
        val response = AuthResponse("token")
        coEvery { authRepository.register(any()) } returns response

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onNameChange(name)
        viewModel.register()

        assert(viewModel.registerResult.value is RegisterResult.Loading)
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.registerResult.value is RegisterResult.Success)
        coVerify { sessionManager.saveAuthToken("token") }
    }

    @Test
    fun `register failure updates state`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val name = "Test User"
        val exception = RuntimeException("Registration failed")
        coEvery { authRepository.register(any()) } throws exception

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onNameChange(name)
        viewModel.register()

        assert(viewModel.registerResult.value is RegisterResult.Loading)
        testDispatcher.scheduler.advanceUntilIdle()
        val errorState = viewModel.registerResult.value as RegisterResult.Error
        assert(errorState.message == "Registration failed")
    }
}