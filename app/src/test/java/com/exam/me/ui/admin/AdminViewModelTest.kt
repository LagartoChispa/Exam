package com.exam.me.ui.admin

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.exam.me.data.local.SessionManager
import com.exam.me.model.User
import com.exam.me.repository.UserRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AdminViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var sessionManager: SessionManager

    private lateinit var adminViewModel: AdminViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { sessionManager.userRole } returns flowOf("ADMIN") // Default role for testing
        adminViewModel = AdminViewModel(application)

        val repositoryField = adminViewModel::class.java.getDeclaredField("userRepository")
        repositoryField.isAccessible = true
        repositoryField.set(adminViewModel, userRepository)

        val sessionManagerField = adminViewModel::class.java.getDeclaredField("sessionManager")
        sessionManagerField.isAccessible = true
        sessionManagerField.set(adminViewModel, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAllUsers success updates adminState`() = runTest {
        val users = listOf(mockk<User>(), mockk<User>())
        coEvery { userRepository.getAllUsers() } returns users

        // The viewmodel calls fetchAllUsers on init, so we just need to re-initialize it after setting mocks
        val viewModel = AdminViewModel(application).apply {
            val repoField = this::class.java.getDeclaredField("userRepository")
            repoField.isAccessible = true
            repoField.set(this, userRepository)
            val sessionField = this::class.java.getDeclaredField("sessionManager")
            sessionField.isAccessible = true
            sessionField.set(this, sessionManager)
        }

        val result = viewModel.adminState.value
        assert(result is AdminState.Success)
        assertEquals(users, (result as AdminState.Success).users)
    }

    @Test
    fun `fetchAllUsers error updates adminState`() = runTest {
        val errorMessage = "Failed to fetch users"
        coEvery { userRepository.getAllUsers() } throws Exception(errorMessage)

        val viewModel = AdminViewModel(application).apply {
            val repoField = this::class.java.getDeclaredField("userRepository")
            repoField.isAccessible = true
            repoField.set(this, userRepository)
            val sessionField = this::class.java.getDeclaredField("sessionManager")
            sessionField.isAccessible = true
            sessionField.set(this, sessionManager)
        }

        val result = viewModel.adminState.value
        assert(result is AdminState.Error)
        assertEquals(errorMessage, (result as AdminState.Error).message)
    }
}