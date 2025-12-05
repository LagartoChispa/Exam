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

sealed class MovieState {
    object Loading : MovieState()
    data class Success(val movies: List<Movie>) : MovieState()
    data class Error(val message: String) : MovieState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository = MovieRepository(RetrofitInstance.api, SessionManager(getApplication()))
    private val sessionManager = SessionManager(getApplication())

    private val _movieState = MutableStateFlow<MovieState>(MovieState.Loading)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredMovies: StateFlow<List<Movie>> = combine(
        _movieState, 
        _searchQuery
    ) { state, query ->
        when (state) {
            is MovieState.Success -> {
                if (query.isBlank()) {
                    state.movies
                } else {
                    state.movies.filter { 
                        it.titulo.contains(query, ignoreCase = true) || 
                        it.director.contains(query, ignoreCase = true)
                    }
                }
            }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    init {
        getMovies()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

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

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutEvent.emit(Unit)
        }
    }
}