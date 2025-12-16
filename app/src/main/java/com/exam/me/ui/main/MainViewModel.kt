package com.exam.me.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Define los posibles estados de autenticación del usuario.
 */
sealed class AuthState {
    /** El estado de autenticación es desconocido, normalmente durante la comprobación inicial. */
    object Unknown : AuthState()
    /** El usuario está autenticado. */
    object Authenticated : AuthState()
    /** El usuario no está autenticado. */
    object Unauthenticated : AuthState()
}

/**
 * ViewModel para [MainActivity], responsable de determinar el estado de autenticación inicial.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())

    // StateFlow para mantener y exponer el estado de autenticación actual.
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState = _authState.asStateFlow()

    /**
     * Inicializa el ViewModel comprobando el estado de autenticación.
     */
    init {
        checkAuthStatus()
    }

    /**
     * Comprueba si existe un token de autenticación en [SessionManager] para determinar
     * si el usuario está autenticado o no.
     */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Obtiene el token de autenticación del session manager.
            val token = sessionManager.authToken.first()
            if (token.isNullOrEmpty()) {
                // Si el token es nulo o está vacío, el usuario no está autenticado.
                _authState.value = AuthState.Unauthenticated
            } else {
                // Si el token existe, el usuario está autenticado.
                _authState.value = AuthState.Authenticated
            }
        }
    }
}