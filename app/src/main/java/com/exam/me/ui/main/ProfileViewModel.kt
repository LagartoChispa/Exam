package com.exam.me.ui.main

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.UserProfile
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.UserRepository
import com.exam.me.util.ImageUploader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Define los posibles estados para la carga del perfil.
 */
sealed class ProfileState {
    /** Indica que el perfil se está cargando actualmente. */
    object Loading : ProfileState()
    /** Indica que el perfil se ha cargado correctamente. */
    data class Success(val profile: UserProfile) : ProfileState()
    /** Indica que se ha producido un error al cargar el perfil. */
    data class Error(val message: String) : ProfileState()
}

/**
 * Representa el estado del formulario de perfil, incluyendo las entradas del usuario y los errores de validación.
 */
data class ProfileFormState(
    val nombre: String = "",
    val email: String = "",
    val nombreError: String? = null
)

/**
 * ViewModel para la pantalla de perfil, responsable de cargar y actualizar los datos del perfil del usuario.
 */
class ProfileViewModel(application: Application, private val imageUploader: ImageUploader = ImageUploader()) : AndroidViewModel(application) {

    private val userRepository = UserRepository(RetrofitInstance.api, SessionManager(getApplication()))

    // StateFlow para mantener y exponer el estado de carga del perfil.
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    // StateFlow para mantener y exponer el estado del formulario de perfil.
    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()

    // StateFlow para mantener y exponer el resultado de la operación de actualización.
    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult: StateFlow<Result<Unit>?> = _updateResult.asStateFlow()

    /**
     * Inicializa el ViewModel cargando el perfil del usuario.
     */
    init {
        loadProfile()
    }

    /**
     * Carga el perfil del usuario actual desde el repositorio y actualiza los estados.
     */
    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val profile = userRepository.getMyProfile()
                _profileState.value = ProfileState.Success(profile)
                _formState.update { it.copy(nombre = profile.nombre, email = profile.user.email) }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    /**
     * Se llama cuando el campo del nombre cambia.
     */
    fun onNameChange(name: String) {
        _formState.update { it.copy(nombre = name) }
    }

    /**
     * Actualiza el perfil del usuario con los datos del formulario.
     */
    fun updateProfile() {
        viewModelScope.launch {
            val currentFormState = _formState.value
            if (currentFormState.nombre.isBlank()) {
                _formState.update { it.copy(nombreError = "Name cannot be empty") }
                return@launch
            }
            _formState.update { it.copy(nombreError = null) }

            val currentState = _profileState.value
            if (currentState is ProfileState.Success) {
                val updatedProfile = currentState.profile.copy(nombre = currentFormState.nombre)
                try {
                    userRepository.updateMyProfile(updatedProfile)
                    _updateResult.value = Result.success(Unit)
                    loadProfile() // Vuelve a cargar los datos del perfil.
                } catch (e: Exception) {
                    _updateResult.value = Result.failure(e)
                }
            }
        }
    }

    /**
     * Sube una nueva imagen de perfil.
     *
     * @param bitmap El mapa de bits de la imagen a subir.
     */
    fun uploadProfileImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val imagePart = imageUploader.bitmapToMultipart(bitmap, "avatar")
                userRepository.uploadProfileAvatar(imagePart)
                loadProfile() // Vuelve a cargar el perfil para obtener la nueva URL de la imagen.
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Image upload failed")
            }
        }
    }
}