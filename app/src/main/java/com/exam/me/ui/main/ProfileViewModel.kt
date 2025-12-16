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

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

data class ProfileFormState(
    val nombre: String = "",
    val email: String = "",
    val nombreError: String? = null
)

class ProfileViewModel(application: Application, private val imageUploader: ImageUploader = ImageUploader()) : AndroidViewModel(application) {

    private val userRepository = UserRepository(RetrofitInstance.api, SessionManager(getApplication()))

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()

    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult: StateFlow<Result<Unit>?> = _updateResult.asStateFlow()

    init {
        loadProfile()
    }

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

    fun onNameChange(name: String) {
        _formState.update { it.copy(nombre = name) }
    }

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
                    loadProfile() // Refresh profile data
                } catch (e: Exception) {
                    _updateResult.value = Result.failure(e)
                }
            }
        }
    }

    fun uploadProfileImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val imagePart = imageUploader.bitmapToMultipart(bitmap, "avatar")
                userRepository.uploadProfileAvatar(imagePart)
                loadProfile() // Refresh profile to get new image URL
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Image upload failed")
            }
        }
    }
}