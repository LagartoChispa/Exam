package com.exam.me.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MovieDetailState {
    object Loading : MovieDetailState()
    data class Success(val movie: Movie) : MovieDetailState()
    data class Error(val message: String) : MovieDetailState()
}

class MovieDetailViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])
    private val movieRepository = MovieRepository(RetrofitInstance.api, SessionManager(getApplication()))

    private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState

    init {
        getMovieDetails()
    }

    private fun getMovieDetails() {
        viewModelScope.launch {
            _movieDetailState.value = MovieDetailState.Loading
            try {
                val movie = movieRepository.getMovieById(movieId)
                _movieDetailState.value = MovieDetailState.Success(movie)
            } catch (e: Exception) {
                _movieDetailState.value = MovieDetailState.Error(e.message ?: "Failed to load movie details")
            }
        }
    }
}