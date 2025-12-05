package com.exam.me.ui.main

import android.app.Application
import com.exam.me.model.Movie
import com.exam.me.repository.MovieRepository
import io.mockk.coEvery
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private lateinit var movieRepository: MovieRepository
    private lateinit var application: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        movieRepository = mockk()

        // This is a workaround to inject mocks into the ViewModel
        viewModel = object : HomeViewModel(application) {
            init {
                this.javaClass.superclass.getDeclaredField("movieRepository").apply {
                    isAccessible = true
                    set(this@object, movieRepository)
                }
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMovies success updates state to Success`() = runTest {
        val mockMovies = listOf(Movie("1", "Title", "Director", "Genre", 120, "Synopsis", "2023", "poster.jpg", "trailer.mp4"))
        coEvery { movieRepository.getMovies() } returns mockMovies

        // The init block of the ViewModel calls getMovies(), so we just need to advance the dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.movieState.value
        assert(state is MovieState.Success)
        assert((state as MovieState.Success).movies.size == 1)
        assert((state as MovieState.Success).movies.first().title == "Title")
    }

    @Test
    fun `getMovies failure updates state to Error`() = runTest {
        val exception = RuntimeException("Failed to load movies")
        coEvery { movieRepository.getMovies() } throws exception

        // The init block of the ViewModel calls getMovies(), so we just need to advance the dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.movieState.value
        assert(state is MovieState.Error)
        assert((state as MovieState.Error).message == "Failed to load movies")
    }
}