package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class MovieRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        apiService = mockk()
        sessionManager = mockk()
        movieRepository = MovieRepository(apiService, sessionManager)
    }

    @Test
    fun `getMovies success calls api with bearer token`() = runBlocking {
        val token = "fake_token"
        val movies = listOf(Movie("1", "Title", "Director", "Genre", 120, "Synopsis", "2023", "poster.jpg", "trailer.mp4"))
        coEvery { sessionManager.authToken } returns flowOf(token)
        coEvery { apiService.getMovies("Bearer $token") } returns movies

        val result = movieRepository.getMovies()

        assert(result.size == 1)
        coVerify { apiService.getMovies("Bearer $token") }
    }

    @Test
    fun `getMovies fails when no token is available`() = runBlocking {
        coEvery { sessionManager.authToken } returns flowOf(null)

        assertFailsWith<Exception>("User not authenticated") {
            movieRepository.getMovies()
        }
    }

    @Test
    fun `getMovieById success calls api with bearer token`() = runBlocking {
        val token = "fake_token"
        val movieId = "123"
        val movie = Movie("123", "Title", "Director", "Genre", 120, "Synopsis", "2023", "poster.jpg", "trailer.mp4")
        coEvery { sessionManager.authToken } returns flowOf(token)
        coEvery { apiService.getMovieById("Bearer $token", movieId) } returns movie

        val result = movieRepository.getMovieById(movieId)

        assert(result.id == movieId)
        coVerify { apiService.getMovieById("Bearer $token", movieId) }
    }
}