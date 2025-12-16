package com.exam.me.ui.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class HomeViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var movieRepository: MovieRepository

    @MockK
    private lateinit var sessionManager: SessionManager

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { sessionManager.userRole } returns flowOf("USER") // Default role
        homeViewModel = HomeViewModel(application)
        
        val repositoryField = homeViewModel::class.java.getDeclaredField("movieRepository")
        repositoryField.isAccessible = true
        repositoryField.set(homeViewModel, movieRepository)

        val sessionManagerField = homeViewModel::class.java.getDeclaredField("sessionManager")
        sessionManagerField.isAccessible = true
        sessionManagerField.set(homeViewModel, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMovies success updates movieState`() = runTest {
        val movies = listOf(mockk<Movie>(), mockk<Movie>())
        coEvery { movieRepository.getMovies() } returns movies

        // The viewmodel calls getMovies on init, so we just need to re-initialize it after setting mocks
        val viewModel = HomeViewModel(application).apply {
            val repoField = this::class.java.getDeclaredField("movieRepository")
            repoField.isAccessible = true
            repoField.set(this, movieRepository)
            val sessionField = this::class.java.getDeclaredField("sessionManager")
            sessionField.isAccessible = true
            sessionField.set(this, sessionManager)
        }

        val result = viewModel.movieState.value
        assert(result is MovieState.Success)
        assertEquals(movies, (result as MovieState.Success).movies)
    }

    @Test
    fun `getMovies error updates movieState`() = runTest {
        val errorMessage = "Failed to load movies"
        coEvery { movieRepository.getMovies() } throws Exception(errorMessage)

        val viewModel = HomeViewModel(application).apply {
            val repoField = this::class.java.getDeclaredField("movieRepository")
            repoField.isAccessible = true
            repoField.set(this, movieRepository)
            val sessionField = this::class.java.getDeclaredField("sessionManager")
            sessionField.isAccessible = true
            sessionField.set(this, sessionManager)
        }

        val result = viewModel.movieState.value
        assert(result is MovieState.Error)
        assertEquals(errorMessage, (result as MovieState.Error).message)
    }

    @Test
    fun `search query filters movies correctly`() = runTest {
        val movie1 = Movie("1", "Title One", "Director A", 2022, 120, "Action", null, null)
        val movie2 = Movie("2", "Title Two", "Director B", 2023, 130, "Comedy", null, null)
        val movies = listOf(movie1, movie2)
        coEvery { movieRepository.getMovies() } returns movies
        
        val viewModel = HomeViewModel(application).apply {
            val repoField = this::class.java.getDeclaredField("movieRepository")
            repoField.isAccessible = true
            repoField.set(this, movieRepository)
            val sessionField = this::class.java.getDeclaredField("sessionManager")
            sessionField.isAccessible = true
            sessionField.set(this, sessionManager)
        }

        viewModel.onSearchQueryChange("Two")

        val filtered = viewModel.filteredMovies.first()
        assertEquals(1, filtered.size)
        assertEquals(movie2, filtered[0])
    }
}