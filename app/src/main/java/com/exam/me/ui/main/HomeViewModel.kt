package com.exam.me.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Define los posibles estados para la carga de películas.
 */
sealed class MovieState {
    /** Indica que las películas se están cargando actualmente. */
    object Loading : MovieState()
    /** Indica que las películas se han cargado correctamente. */
    data class Success(val movies: List<Movie>) : MovieState()
    /** Indica que se ha producido un error al cargar las películas. */
    data class Error(val message: String) : MovieState()
}

/**
 * ViewModel para [HomeScreen], responsable de obtener y gestionar los datos de las películas,
 * gestionar el estado de autenticación del usuario y la funcionalidad de búsqueda.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository = MovieRepository(RetrofitInstance.api, SessionManager(getApplication()))
    private val sessionManager = SessionManager(getApplication())

    // StateFlow para mantener y exponer el estado actual de la carga de películas.
    private val _movieState = MutableStateFlow<MovieState>(MovieState.Loading)
    val movieState: StateFlow<MovieState> = _movieState.asStateFlow()

    // StateFlow para mantener y exponer la consulta de búsqueda actual.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Expone el rol del usuario actual desde el [SessionManager].
     */
    val userRole: StateFlow<String?> = sessionManager.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Un flujo de películas que se filtran en función de la [_searchQuery] actual.
     * Combina el último estado de la película y la consulta de búsqueda para producir una lista filtrada.
     */
    val filteredMovies: StateFlow<List<Movie>> = combine(
        _movieState, 
        _searchQuery
    ) { state, query ->
        when (state) {
            is MovieState.Success -> {
                if (query.isBlank()) {
                    // Si la consulta está en blanco, devuelve la lista completa de películas.
                    state.movies
                } else {
                    // De lo contrario, filtra las películas en las que el título o el director contienen la consulta.
                    state.movies.filter { 
                        it.titulo.contains(query, ignoreCase = true) || 
                        it.director.contains(query, ignoreCase = true)
                    }
                }
            }
            // Si el estado no es Success (p. ej., Loading o Error), devuelve una lista vacía.
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SharedFlow para señalar un evento de cierre de sesión a la interfaz de usuario.
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    /**
     * Inicializa el ViewModel obteniendo la lista de películas.
     */
    init {
        getMovies()
    }

    /**
     * Se llama cuando el texto de la consulta de búsqueda cambia.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Obtiene la lista de películas del repositorio y actualiza el [movieState].
     */
    private fun getMovies() {
        viewModelScope.launch {
            _movieState.value = MovieState.Loading
            try {
                val movies = movieRepository.getMovies()
                _movieState.value = MovieState.Success(movies)
            } catch (e: Exception) {
                _movieState.value = MovieState.Error(e.message ?: "Failed to load movies")
            }
        }
    }

    /**
     * Borra la sesión del usuario y emite un evento de cierre de sesión.
     */
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutEvent.emit(Unit)
        }
    }
}