package com.exam.me.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exam.me.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddMovie: () -> Unit
) {
    val adminState by adminViewModel.adminState.collectAsState()
    val userRole by adminViewModel.userRole.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = adminState) {
                is AdminState.Loading -> {
                    CircularProgressIndicator()
                }
                is AdminState.Success -> {
                    UserList(users = state.users, userRole = userRole, onNavigateToAddMovie = onNavigateToAddMovie)
                }
                is AdminState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun UserList(users: List<User>, userRole: String?, onNavigateToAddMovie: () -> Unit) {
    Column {
        if (userRole == "ADMIN") {
            Button(onClick = onNavigateToAddMovie, modifier = Modifier.padding(16.dp)) {
                Text("Add New Movie")
            }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(users, key = { it.id }) { user ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${user.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                        Text("Role: ${user.role}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}