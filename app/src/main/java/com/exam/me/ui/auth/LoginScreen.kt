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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult) {
        if (loginResult is LoginResult.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

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
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.isFormValid && loginResult !is LoginResult.Loading
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Register",
            modifier = Modifier.clickable { onNavigateToRegister() }
        )

        Spacer(modifier = Modifier.height(16.dp))

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