package com.exam.me.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Una pantalla Composable para el inicio de sesión de usuarios.
 *
 * @param viewModel El [LoginViewModel] que gestiona el estado y la lógica de esta pantalla.
 * @param onNavigateToRegister Una función de devolución de llamada para navegar a la pantalla de registro.
 * @param onNavigateToForgotPassword Una función de devolución de llamada para navegar a la pantalla de olvido de contraseña.
 * @param onLoginSuccess Una función de devolución de llamada que se invoca cuando el inicio de sesión se realiza correctamente.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    // Observa el estado del formulario y el resultado del inicio de sesión desde el viewModel.
    val formState by viewModel.formState.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()

    // Se activa cuando cambia el resultado del inicio de sesión. Si el inicio de sesión es exitoso, navega.
    LaunchedEffect(loginResult) {
        if (loginResult is LoginResult.Success) {
            onLoginSuccess()
        }
    }

    // La interfaz de usuario de la pantalla de inicio de sesión, dispuesta en una columna.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Campo de texto para el email del usuario.
        OutlinedTextField(
            value = formState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.emailError != null,
            trailingIcon = { if (formState.emailError != null) Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error) },
            supportingText = { if (formState.emailError != null) Text(text = formState.emailError!!, color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la contraseña del usuario.
        OutlinedTextField(
            value = formState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = formState.passwordError != null,
            trailingIcon = { if (formState.passwordError != null) Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error) },
            supportingText = { if (formState.passwordError != null) Text(text = formState.passwordError!!, color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Texto en el que se puede hacer clic para navegar a la pantalla de olvido de contraseña.
        Text(
            text = "Forgot Password?",
            modifier = Modifier
                .clickable { onNavigateToForgotPassword() }
                .align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para activar el proceso de inicio de sesión.
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.isFormValid && loginResult !is LoginResult.Loading
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto en el que se puede hacer clic para navegar a la pantalla de registro.
        Text(
            text = "Don't have an account? Register",
            modifier = Modifier.clickable { onNavigateToRegister() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra un indicador de carga o un mensaje de error basado en el resultado del inicio de sesión.
        when (val result = loginResult) {
            is LoginResult.Loading -> {
                CircularProgressIndicator()
            }
            is LoginResult.Error -> {
                Text(result.message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}