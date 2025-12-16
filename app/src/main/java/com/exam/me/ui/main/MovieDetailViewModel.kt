package com.exam.me.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.ExternalApiInstance
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.ExternalMovieRepository
import com.exam.me.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Define los posibles estados para la carga de los detalles de una película.
 */
sealed class MovieDetailState {
    /** Indica que los detalles de la película se están cargando actualmente. */
    object Loading : MovieDetailState()
    /** Indica que los detalles de la película se han cargado correctamente. */
    data class Success(val movie: Movie) : MovieDetailState()
    /** Indica que se ha producido un error al cargar los detalles de la película. */
    data class Error(val message: String) : MovieDetailState()
}

/**
 * ViewModel para [MovieDetailScreen], responsable de obtener los detalles de una película específica.
 */
class MovieDetailViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    // Obtiene el ID de la película de los argumentos de navegación.
    private val movieId: String = checkNotNull(savedStateHandle["movieId"])
    private val movieRepository = MovieRepository(RetrofitInstance.api, SessionManager(getApplication()))
    private val externalMovieRepository = ExternalMovieRepository(ExternalApiInstance.api)

    // StateFlow para mantener y exponer el estado de carga de los detalles de la película.
    private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState

    /**
     * Inicializa el ViewModel obteniendo los detalles de la película.
     */
    init {
        getMovieDetails()
    }

    /**
     * Obtiene los detalles de la película del repositorio, incluyendo la URL del póster de una API externa.
     */
    private fun getMovieDetails() {
        viewModelScope.launch {
            _movieDetailState.value = MovieDetailState.Loading
            try {
                val movie = movieRepository.getMovieById(movieId)
                // Busca la ruta del póster de la película en la API externa.
                val posterPath = externalMovieRepository.findMoviePoster(movie.titulo)
                if (posterPath != null) {
                    // Construye la URL completa del póster.
                    movie.posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
                }
                _movieDetailState.value = MovieDetailState.Success(movie)
            } catch (e: Exception) {
                _movieDetailState.value = MovieDetailState.Error(e.message ?: "Failed to load movie details")
            }
        }
    }
}