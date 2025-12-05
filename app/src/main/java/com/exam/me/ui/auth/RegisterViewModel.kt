package com.exam.me.ui.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.AuthResponse
import com.exam.me.model.RegisterRequest
import com.exam.me.repository.AuthRepository
import com.exam.me.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class RegisterResult {
    object Idle : RegisterResult()
    object Loading : RegisterResult()
    data class Success(val authResponse: AuthResponse) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

// Simplified form state - only name, email, password
data class RegisterFormState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())
    private val authRepository = AuthRepository(RetrofitInstance.api, sessionManager)

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    private val _registerResult = MutableStateFlow<RegisterResult>(RegisterResult.Idle)
    val registerResult: StateFlow<RegisterResult> = _registerResult.asStateFlow()

    fun onNameChange(name: String) {
        _formState.update { it.copy(nombre = name) }
        validateForm()
    }

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
        validateForm()
    }

    fun onPasswordChange(password: String) {
        _formState.update { it.copy(password = password) }
        validateForm()
    }

    private fun validateForm() {
        val state = _formState.value
        val nameError = if (state.nombre.isBlank()) "Nombre is required" else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) "Invalid email" else null
        val passwordError = if (state.password.length < 6) "Password must be at least 6 characters" else null

        _formState.update { it.copy(
            nombreError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            isFormValid = nameError == null && emailError == null && passwordError == null
        ) }
    }

    fun register() {
        validateForm()
        if (!_formState.value.isFormValid) return

        viewModelScope.launch {
            _registerResult.value = RegisterResult.Loading
            try {
                val state = _formState.value
                val request = RegisterRequest(
                    nombre = state.nombre,
                    email = state.email,
                    password = state.password
                )
                val authResponse = authRepository.register(request)
                sessionManager.saveSession(authResponse.accessToken, authResponse.user.role)
                _registerResult.value = RegisterResult.Success(authResponse)
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}