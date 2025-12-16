package com.exam.me.repository

import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para el [MovieRepository].
 */
class MovieRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var movieRepository: MovieRepository

    /**
     * Configura el entorno de prueba antes de cada prueba.
     */
    @Before
    fun setUp() {
        apiService = mockk()
        sessionManager = mockk()
        movieRepository = MovieRepository(apiService, sessionManager)
    }

    /**
     * Prueba que `getMovies` llama con éxito a la API con el token de portador y devuelve una lista de películas.
     */
    @Test
    fun `getMovies success calls api with bearer token`() = runTest {
        // Dado un token falso y una lista de películas
        val token = "fake_token"
        val movies = listOf(Movie(id = "1", titulo = "Title", director = "Director", anio = 2023, duracion = 120, genero = "Genre", imagen = "poster.jpg", imagenThumbnail = null))
        // Simula el session manager para que devuelva el token falso
        coEvery { sessionManager.authToken } returns flowOf(token)
        // Simula el servicio api para que devuelva la lista de películas cuando se le llama con el token de portador
        coEvery { apiService.getMovies("Bearer $token") } returns movies

        // Cuando se llama a getMovies
        val result = movieRepository.getMovies()

        // Entonces el resultado debe contener una película y el servicio api debe ser llamado con el token de portador correcto
        assert(result.size == 1)
        coVerify { apiService.getMovies("Bearer $token") }
    }

    /**
     * Prueba que `getMovies` falla cuando no hay un token de autenticación disponible.
     */
    @Test
    fun `getMovies fails when no token is available`() = runTest {
        // Dado que el session manager no devuelve ningún token
        coEvery { sessionManager.authToken } returns flowOf(null)

        try {
            // Cuando se llama a getMovies, se debe lanzar una excepción
            movieRepository.getMovies()
            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            // Entonces el mensaje de la excepción debe ser "User not authenticated"
            assertEquals("User not authenticated", e.message)
        }
    }

    /**
     * Prueba que `getMovieById` llama con éxito a la API con el token de portador y devuelve una película.
     */
    @Test
    fun `getMovieById success calls api with bearer token`() = runTest {
        // Dado un token falso, un ID de película y un objeto de película
        val token = "fake_token"
        val movieId = "123"
        val movie = Movie(id = "123", titulo = "Title", director = "Director", anio = 2023, duracion = 120, genero = "Genre", imagen = "poster.jpg", imagenThumbnail = null)
        // Simula el session manager para que devuelva el token falso
        coEvery { sessionManager.authToken } returns flowOf(token)
        // Simula el servicio api para que devuelva la película cuando se le llama con el token de portador y el ID de la película
        coEvery { apiService.getMovieById("Bearer $token", movieId) } returns movie

        // Cuando se llama a getMovieById con el ID de la película
        val result = movieRepository.getMovieById(movieId)

        // Entonces el resultado debe ser la película y el servicio api debe ser llamado con el token de portador y el ID de la película correctos
        assert(result.id == movieId)
        coVerify { apiService.getMovieById("Bearer $token", movieId) }
    }
}