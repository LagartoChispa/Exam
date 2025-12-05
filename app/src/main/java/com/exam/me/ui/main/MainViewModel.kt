package com.exam.me.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class AuthState {
    object Unknown : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val token = sessionManager.authToken.first()
            if (token.isNullOrEmpty()) {
                _authState.value = AuthState.Unauthenticated
            } else {
                _authState.value = AuthState.Authenticated
            }
        }
    }
}