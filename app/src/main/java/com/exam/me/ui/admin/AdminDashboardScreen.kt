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

/**
 * Panel de administración que muestra una lista de usuarios y proporciona acciones administrativas.
 *
 * @param adminViewModel El [AdminViewModel] que gestiona el estado y la lógica de esta pantalla.
 * @param onNavigateBack Devolución de llamada para navegar a la pantalla anterior.
 * @param onNavigateToAddMovie Devolución de llamada para navegar a la pantalla de agregar película.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddMovie: () -> Unit
) {
    // Recopila los diversos estados del ViewModel.
    val adminState by adminViewModel.adminState.collectAsState()
    val userRole by adminViewModel.userRole.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
            // Muestra el contenido en función del estado del administrador.
            when (val state = adminState) {
                is AdminState.Loading -> {
                    // Muestra un indicador de carga mientras se cargan los datos.
                    CircularProgressIndicator()
                }
                is AdminState.Success -> {
                    // Muestra la lista de usuarios cuando la carga es exitosa.
                    UserList(users = state.users, userRole = userRole, onNavigateToAddMovie = onNavigateToAddMovie)
                }
                is AdminState.Error -> {
                    // Muestra un mensaje de error si la carga de datos falla.
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * Muestra una lista de usuarios.
 *
 * @param users La lista de [User] a mostrar.
 * @param userRole El rol del usuario actual.
 * @param onNavigateToAddMovie Devolución de llamada para navegar a la pantalla de agregar película.
 */
@Composable
fun UserList(users: List<User>, userRole: String?, onNavigateToAddMovie: () -> Unit) {
    Column {
        // Muestra el botón "Agregar nueva película" solo a los administradores.
        if (userRole == "ADMIN") {
            Button(onClick = onNavigateToAddMovie, modifier = Modifier.padding(16.dp)) {
                Text("Agregar Nueva Película")
            }
        }
        // Muestra la lista de usuarios en una LazyColumn.
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(users, key = { it.id }) { user ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${user.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                        Text("Rol: ${user.role}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}