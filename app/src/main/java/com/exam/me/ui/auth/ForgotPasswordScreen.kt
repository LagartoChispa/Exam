package com.exam.me.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Una pantalla Composable para el proceso de olvido de contraseña.
 *
 * @param viewModel El [ForgotPasswordViewModel] que gestiona el estado y la lógica de esta pantalla.
 * @param onNavigateBack Una función de devolución de llamada para navegar a la pantalla anterior.
 * @param onPasswordResetSent Una función de devolución de llamada que se invoca cuando el enlace de restablecimiento de contraseña se ha enviado correctamente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onPasswordResetSent: () -> Unit
) {
    // Observa el estado del formulario y el estado de restablecimiento desde el viewModel.
    val formState by viewModel.formState.collectAsState()
    val resetState by viewModel.resetState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Si el enlace se ha enviado correctamente, muestra un mensaje de éxito.
            if (resetState is ForgotPasswordState.Success) {
                Text("A password reset link has been sent to your email.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onPasswordResetSent) {
                    Text("Back to Login")
                }
            } else {
                // De lo contrario, muestra el formulario para enviar el enlace de restablecimiento.
                Text("Enter your email to receive a password reset link.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(32.dp))

                // Campo de texto para el email.
                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.emailError != null,
                    trailingIcon = { if (formState.emailError != null) Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error) },
                    supportingText = { if (formState.emailError != null) Text(text = formState.emailError!!, color = MaterialTheme.colorScheme.error) }
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Botón para enviar el enlace de restablecimiento.
                Button(
                    onClick = { viewModel.sendResetLink() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.isFormValid && resetState !is ForgotPasswordState.Loading
                ) {
                    Text("Send Reset Link")
                }

                // Muestra un indicador de carga o un mensaje de error basado en el estado de restablecimiento.
                when (val result = resetState) {
                    is ForgotPasswordState.Loading -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                    is ForgotPasswordState.Error -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(result.message, color = MaterialTheme.colorScheme.error)
                    }
                    else -> { /* Idle or Success is handled elsewhere */ }
                }
            }
        }
    }
}