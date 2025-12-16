package com.exam.me.ui.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.AuthResponse
import com.exam.me.model.LoginRequest
import com.exam.me.repository.AuthRepository
import com.exam.me.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Define los posibles resultados de la operación de inicio de sesión.
 */
sealed class LoginResult {
    object Idle : LoginResult() // El estado inicial antes de que comience el inicio de sesión.
    object Loading : LoginResult() // Indica que la operación de inicio de sesión está en curso.
    data class Success(val authResponse: AuthResponse) : LoginResult() // Indica un inicio de sesión exitoso.
    data class Error(val message: String) : LoginResult() // Indica que se ha producido un error durante el inicio de sesión.
}

/**
 * Representa el estado del formulario de inicio de sesión, incluyendo las entradas del usuario y los errores de validación.
 */
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false
)

/**
 * ViewModel para la pantalla de inicio de sesión, que gestiona la lógica de negocio y el estado de la interfaz de usuario.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())
    private val authRepository = AuthRepository(RetrofitInstance.api, sessionManager)

    // StateFlow para mantener y exponer el estado del formulario de inicio de sesión.
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    // StateFlow para mantener y exponer el resultado de la operación de inicio de sesión.
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

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
        val email = _formState.value.email
        val password = _formState.value.password

        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Email inválido" else null
        val passwordError = if (password.isBlank()) "La contraseña no puede estar vacía" else null

        _formState.update { it.copy(
            emailError = emailError,
            passwordError = passwordError,
            isFormValid = emailError == null && passwordError == null
        ) }
    }

    /**
     * Inicia el proceso de inicio de sesión.
     */
    fun login() {
        validateForm()
        if (!_formState.value.isFormValid) return

        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            try {
                val request = LoginRequest(
                    email = _formState.value.email,
                    password = _formState.value.password
                )
                val authResponse = authRepository.login(request)
                if (authResponse.user != null) {
                    sessionManager.saveSession(authResponse.accessToken, authResponse.user.role)
                    _loginResult.value = LoginResult.Success(authResponse)
                } else {
                    _loginResult.value = LoginResult.Error("Error de inicio de sesión: no se devolvieron los datos del usuario")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "Ocurrió un error inesperado")
            }
        }
    }
}