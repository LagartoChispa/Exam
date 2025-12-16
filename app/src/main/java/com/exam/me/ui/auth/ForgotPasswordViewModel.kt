package com.exam.me.ui.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.ForgotPasswordRequest
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Define los posibles estados del proceso de olvido de contraseña.
 */
sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState() // El estado inicial antes de que comience el proceso.
    object Loading : ForgotPasswordState() // Indica que el proceso está en curso.
    object Success : ForgotPasswordState() // Indica que el enlace de restablecimiento de contraseña se ha enviado correctamente.
    data class Error(val message: String) : ForgotPasswordState() // Indica que se ha producido un error.
}

/**
 * Representa el estado del formulario de olvido de contraseña.
 */
data class ForgotPasswordFormState(
    val email: String = "",
    val emailError: String? = null,
    val isFormValid: Boolean = false
)

/**
 * ViewModel para la pantalla de olvido de contraseña.
 */
class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(RetrofitInstance.api, SessionManager(getApplication()))

    // StateFlow para mantener y exponer el estado del formulario.
    private val _formState = MutableStateFlow(ForgotPasswordFormState())
    val formState: StateFlow<ForgotPasswordFormState> = _formState.asStateFlow()

    // StateFlow para mantener y exponer el estado del proceso de restablecimiento de contraseña.
    private val _resetState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val resetState: StateFlow<ForgotPasswordState> = _resetState.asStateFlow()

    /**
     * Se llama cuando el campo del email cambia.
     */
    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
        validateForm()
    }

    /**
     * Valida el campo de email.
     */
    private fun validateForm() {
        val email = _formState.value.email
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Email inválido" else null
        _formState.update { it.copy(
            emailError = emailError,
            isFormValid = emailError == null
        ) }
    }

    /**
     * Envía un enlace de restablecimiento de contraseña al email proporcionado.
     */
    fun sendResetLink() {
        validateForm()
        if (!_formState.value.isFormValid) return

        viewModelScope.launch {
            _resetState.value = ForgotPasswordState.Loading
            try {
                val request = ForgotPasswordRequest(email = _formState.value.email)
                authRepository.forgotPassword(request)
                _resetState.value = ForgotPasswordState.Success
            } catch (e: Exception) {
                _resetState.value = ForgotPasswordState.Error(e.message ?: "Ocurrió un error inesperado")
            }
        }
    }
}