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

/**
 * Define los posibles resultados de la operación de registro.
 */
sealed class RegisterResult {
    object Idle : RegisterResult() // El estado inicial antes de que comience el registro.
    object Loading : RegisterResult() // Indica que la operación de registro está en curso.
    data class Success(val authResponse: AuthResponse) : RegisterResult() // Indica un registro exitoso.
    data class Error(val message: String) : RegisterResult() // Indica que se ha producido un error durante el registro.
}

/**
 * Representa el estado del formulario de registro, incluyendo las entradas del usuario y los errores de validación.
 */
data class RegisterFormState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false
)

/**
 * ViewModel para la pantalla de registro, que gestiona la lógica de negocio y el estado de la interfaz de usuario.
 */
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())
    private val authRepository = AuthRepository(RetrofitInstance.api, sessionManager)

    // StateFlow para mantener y exponer el estado del formulario de registro.
    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    // StateFlow para mantener y exponer el resultado de la operación de registro.
    private val _registerResult = MutableStateFlow<RegisterResult>(RegisterResult.Idle)
    val registerResult: StateFlow<RegisterResult> = _registerResult.asStateFlow()

    /**
     * Se llama cuando el campo del nombre cambia.
     */
    fun onNameChange(name: String) {
        _formState.update { it.copy(nombre = name) }
        validateForm()
    }

    /**
     * Se llama cuando el campo del email cambia.
     */
    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
        validateForm()
    }

    /**
     * Se llama cuando el campo de la contraseña cambia.
     */
    fun onPasswordChange(password: String) {
        _formState.update { it.copy(password = password) }
        validateForm()
    }

    /**
     * Valida los campos del formulario y actualiza el estado del formulario con cualquier error.
     */
    private fun validateForm() {
        val state = _formState.value
        val nameError = if (state.nombre.isBlank()) "El nombre es obligatorio" else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) "Email inválido" else null
        val passwordError = if (state.password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null

        _formState.update { it.copy(
            nombreError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            isFormValid = nameError == null && emailError == null && passwordError == null
        ) }
    }

    /**
     * Inicia el proceso de registro.
     */
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
                if (authResponse.user != null) {
                    sessionManager.saveSession(authResponse.accessToken, authResponse.user.role)
                    _registerResult.value = RegisterResult.Success(authResponse)
                } else {
                    _registerResult.value = RegisterResult.Error("Error de registro: no se devolvieron los datos del usuario")
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error(e.message ?: "Ocurrió un error inesperado")
            }
        }
    }
}