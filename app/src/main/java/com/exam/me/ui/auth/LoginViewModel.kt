package com.exam.me.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.model.AuthResponse
import com.exam.me.model.LoginRequest
import com.exam.me.repository.AuthRepository
import com.exam.me.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val authResponse: AuthResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository(RetrofitInstance.api)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResponse = authRepository.login(LoginRequest(email, password))
                _loginState.value = LoginState.Success(authResponse)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}