package com.exam.me

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.exam.me.ui.main.AuthState
import com.exam.me.ui.main.MainViewModel
import com.exam.me.ui.navigation.AppNavigation
import com.exam.me.ui.navigation.Screen
import com.exam.me.ui.theme.ExamTheme

/**
 * La actividad principal de la aplicación.
 */
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExamTheme {
                // Contenedor de la superficie principal de la aplicación.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Observa el estado de autenticación desde el viewModel.
                    val authState by mainViewModel.authState.collectAsState()

                    // Determina la pantalla inicial en función del estado de autenticación.
                    when (authState) {
                        AuthState.Unknown -> {
                            // Muestra un indicador de carga mientras se determina el estado de autenticación.
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        AuthState.Authenticated -> {
                            // Si está autenticado, comienza en la pantalla de inicio.
                            AppNavigation(startDestination = Screen.Home.route)
                        }
                        AuthState.Unauthenticated -> {
                            // Si no está autenticado, comienza en la pantalla de inicio de sesión.
                            AppNavigation(startDestination = Screen.Login.route)
                        }
                    }
                }
            }
        }
    }
}