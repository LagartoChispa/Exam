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

// New Registration Screen Composable
@Composable
fun RegistroSc(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val registerResult by viewModel.registerResult.collectAsState()

    LaunchedEffect(registerResult) {
        if (registerResult is RegisterResult.Success) {
            onRegisterSuccess()
        }
    }

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

        Button(
            onClick = { viewModel.register() },
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.isFormValid && registerResult !is RegisterResult.Loading
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable { onNavigateToLogin() }
        )

        Spacer(modifier = Modifier.height(16.dp))

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