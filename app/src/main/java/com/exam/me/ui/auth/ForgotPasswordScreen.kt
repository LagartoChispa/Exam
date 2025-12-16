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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onPasswordResetSent: () -> Unit
) {
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
            if (resetState is ForgotPasswordState.Success) {
                Text("A password reset link has been sent to your email.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onPasswordResetSent) {
                    Text("Back to Login")
                }
            } else {
                Text("Enter your email to receive a password reset link.", style = MaterialTheme.typography.bodyLarge)
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
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.sendResetLink() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.isFormValid && resetState !is ForgotPasswordState.Loading
                ) {
                    Text("Send Reset Link")
                }

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