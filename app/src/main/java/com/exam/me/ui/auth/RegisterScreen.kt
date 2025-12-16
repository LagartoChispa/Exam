package com.exam.me.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * Una pantalla Composable para el registro de usuarios.
 *
 * @param viewModel El [RegisterViewModel] que gestiona el estado y la lógica de esta pantalla.
 * @param onNavigateToLogin Una función de devolución de llamada para navegar a la pantalla de inicio de sesión.
 * @param onRegisterSuccess Una función de devolución de llamada que se invoca cuando el registro se realiza correctamente.
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // Observa el estado del formulario y el resultado del registro desde el viewModel.
    val formState by viewModel.formState.collectAsState()
    val registerResult by viewModel.registerResult.collectAsState()

    // Se activa cuando cambia el resultado del registro. Si el registro es exitoso, navega.
    LaunchedEffect(registerResult) {
        if (registerResult is RegisterResult.Success) {
            onRegisterSuccess()
        }
    }

    // La interfaz de usuario de la pantalla de registro, dispuesta en una columna desplazable.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Campo de texto para el nombre de usuario.
        OutlinedTextField(
            value = formState.nombre,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.nombreError != null,
            trailingIcon = { if (formState.nombreError != null) Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error) },
            supportingText = { if (formState.nombreError != null) Text(text = formState.nombreError!!, color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(16.dp))

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
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para activar el proceso de registro.
        Button(
            onClick = { viewModel.register() },
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.isFormValid && registerResult !is RegisterResult.Loading
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto en el que se puede hacer clic para navegar a la pantalla de inicio de sesión.
        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable { onNavigateToLogin() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra un indicador de carga o un mensaje de error basado en el resultado del registro.
        when (val result = registerResult) {
            is RegisterResult.Loading -> {
                CircularProgressIndicator()
            }
            is RegisterResult.Error -> {
                Text(result.message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}