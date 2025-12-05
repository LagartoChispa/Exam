package com.exam.me.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.model.AuthResponse
import com.exam.me.model.RegisterRequest
import com.exam.me.repository.AuthRepository
import com.exam.me.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val authResponse: AuthResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository(RetrofitInstance.api)

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val authResponse = authRepository.register(RegisterRequest(name, email, password))
                _registerState.value = RegisterState.Success(authResponse)
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}