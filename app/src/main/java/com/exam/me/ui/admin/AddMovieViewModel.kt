package com.exam.me.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Define los posibles resultados de la operación de agregar película.
 */
sealed class AddMovieResult {
    /** El estado inicial antes de que comience la operación. */
    object Idle : AddMovieResult()
    /** Indica que la operación está en curso. */
    object Loading : AddMovieResult()
    /** Indica que la película se ha agregado correctamente. */
    data class Success(val movie: Movie) : AddMovieResult()
    /** Indica que se ha producido un error durante la operación. */
    data class Error(val message: String) : AddMovieResult()
}

/**
 * Representa el estado del formulario para agregar una nueva película.
 */
data class AddMovieFormState(
    val titulo: String = "",
    val director: String = "",
    val anio: String = "",
    val duracion: String = "",
    val genero: String = "",
    val tituloError: String? = null,
    val directorError: String? = null,
    val anioError: String? = null,
    val duracionError: String? = null,
    val generoError: String? = null,
    val isFormValid: Boolean = false
)

/**
 * ViewModel para la pantalla de agregar película, responsable de la lógica de validación y creación de películas.
 */
class AddMovieViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(RetrofitInstance.api, SessionManager(getApplication()))

    // StateFlow para mantener y exponer el estado del formulario.
    private val _formState = MutableStateFlow(AddMovieFormState())
    val formState: StateFlow<AddMovieFormState> = _formState.asStateFlow()

    // StateFlow para mantener y exponer el resultado de la operación de agregar película.
    private val _addMovieResult = MutableStateFlow<AddMovieResult>(AddMovieResult.Idle)
    val addMovieResult: StateFlow<AddMovieResult> = _addMovieResult.asStateFlow()

    /**
     * Se llama cuando cambia cualquier campo del formulario.
     */
    fun onFormChange(titulo: String, director: String, anio: String, duracion: String, genero: String) {
        _formState.update { it.copy(titulo = titulo, director = director, anio = anio, duracion = duracion, genero = genero) }
        validateForm()
    }

    /**
     * Valida los campos del formulario y actualiza el estado del formulario con cualquier error.
     */
    private fun validateForm() {
        val state = _formState.value
        val anioInt = state.anio.toIntOrNull()
        val duracionInt = state.duracion.toIntOrNull()

        val tituloError = if (state.titulo.isBlank()) "El título es obligatorio" else null
        val directorError = if (state.director.isBlank()) "El director es obligatorio" else null
        val generoError = if (state.genero.isBlank()) "El género es obligatorio" else null
        val anioError = if (anioInt == null || anioInt <= 1800) "Año inválido" else null
        val duracionError = if (duracionInt == null || duracionInt <= 0) "Duración inválida" else null

        _formState.update {
            it.copy(
                tituloError = tituloError,
                directorError = directorError,
                anioError = anioError,
                duracionError = duracionError,
                generoError = generoError,
                isFormValid = tituloError == null && directorError == null && anioError == null && duracionError == null && generoError == null
            )
        }
    }

    /**
     * Inicia el proceso de creación de la película.
     */
    fun createMovie() {
        validateForm()
        if (!_formState.value.isFormValid) return

        viewModelScope.launch {
            _addMovieResult.value = AddMovieResult.Loading
            try {
                val state = _formState.value
                val movie = Movie(
                    id = "", // El ID es generado por el backend
                    titulo = state.titulo,
                    director = state.director,
                    anio = state.anio.toInt(),
                    duracion = state.duracion.toInt(),
                    genero = state.genero,
                    imagen = null,
                    imagenThumbnail = null
                )
                val result = userRepository.createMovie(movie)
                _addMovieResult.value = AddMovieResult.Success(result)
            } catch (e: Exception) {
                _addMovieResult.value = AddMovieResult.Error(e.message ?: "Ocurrió un error inesperado")
            }
        }
    }
}