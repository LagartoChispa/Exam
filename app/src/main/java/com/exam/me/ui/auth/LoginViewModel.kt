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

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val authResponse: AuthResponse) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())
    private val authRepository = AuthRepository(RetrofitInstance.api, sessionManager)

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
        validateForm()
    }

    fun onPasswordChange(password: String) {
        _formState.update { it.copy(password = password) }
        validateForm()
    }

    private fun validateForm() {
        val email = _formState.value.email
        val password = _formState.value.password

        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid email" else null
        val passwordError = if (password.isBlank()) "Password cannot be empty" else null

        _formState.update { it.copy(
            emailError = emailError,
            passwordError = passwordError,
            isFormValid = emailError == null && passwordError == null
        ) }
    }

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
                // The login response now contains the user and token, so no extra call is needed.
                val authResponse = authRepository.login(request)
                sessionManager.saveSession(authResponse.accessToken, authResponse.user.role)
                _loginResult.value = LoginResult.Success(authResponse)
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}