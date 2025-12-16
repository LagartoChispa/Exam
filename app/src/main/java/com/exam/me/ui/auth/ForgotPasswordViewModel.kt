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

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

data class ForgotPasswordFormState(
    val email: String = "",
    val emailError: String? = null,
    val isFormValid: Boolean = false
)

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(RetrofitInstance.api, SessionManager(getApplication()))

    private val _formState = MutableStateFlow(ForgotPasswordFormState())
    val formState: StateFlow<ForgotPasswordFormState> = _formState.asStateFlow()

    private val _resetState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val resetState: StateFlow<ForgotPasswordState> = _resetState.asStateFlow()

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
        validateForm()
    }

    private fun validateForm() {
        val email = _formState.value.email
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid email" else null
        _formState.update { it.copy(
            emailError = emailError,
            isFormValid = emailError == null
        ) }
    }

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
                _resetState.value = ForgotPasswordState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}