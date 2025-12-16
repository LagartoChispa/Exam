package com.exam.me.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exam.me.data.local.SessionManager
import com.exam.me.model.Movie
import kotlinx.coroutines.launch

/**
 * La pantalla principal que muestra un catálogo de películas.
 *
 * @param homeViewModel El [HomeViewModel] que proporciona datos y gestiona la lógica de esta pantalla.
 * @param onMovieClick Devolución de llamada para cuando se hace clic en una película, pasando el ID de la película.
 * @param onLogout Devolución de llamada para gestionar el evento de cierre de sesión.
 * @param onNavigateToProfile Devolución de llamada para navegar a la pantalla de perfil.
 * @param onNavigateToAdminDashboard Devolución de llamada para navegar al panel de administración.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onMovieClick: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit
) {
    // Recopila los diversos estados del ViewModel.
    val filteredMovies by homeViewModel.filteredMovies.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val movieState by homeViewModel.movieState.collectAsState()
    val userRole by homeViewModel.userRole.collectAsState()
    val sessionManager = SessionManager(LocalContext.current)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo CinePlus") },
                actions = {
                    // Muestra el botón de perfil para los roles de USUARIO y ADMIN.
                    if (userRole == "USER" || userRole == "ADMIN") {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(Icons.Filled.Person, contentDescription = "Perfil")
                        }
                    }
                    // Muestra el botón del panel de administración para los roles de ADMIN y SUPERVISOR.
                    if (userRole == "ADMIN" || userRole == "SUPERVISOR") {
                        IconButton(onClick = onNavigateToAdminDashboard) {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Panel de Administración")
                        }
                    }
                    // Botón para cerrar sesión.
                    IconButton(onClick = {
                        scope.launch {
                            sessionManager.clearSession()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo de texto para buscar películas por título o director.
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { homeViewModel.onSearchQueryChange(it) },
                label = { Text("Buscar por título o director...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            // Contenedor para el contenido principal, que cambia según el estado de la carga de la película.
            Box(modifier = Modifier.weight(1f)) {
                when (movieState) {
                    is MovieState.Loading -> {
                        // Muestra un indicador de carga mientras se obtienen las películas.
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is MovieState.Success -> {
                        if (filteredMovies.isEmpty() && searchQuery.isNotEmpty()) {
                            // Muestra un mensaje si no se encuentran resultados de búsqueda.
                            Text("No se encontraron resultados", modifier = Modifier.align(Alignment.Center))
                        } else {
                            // Muestra la cuadrícula de películas si la carga es exitosa.
                            MovieGrid(movies = filteredMovies, onMovieClick = onMovieClick)
                        }
                    }
                    is MovieState.Error -> {
                        // Muestra un mensaje de error si la obtención de películas falla.
                        Text(
                            text = (movieState as MovieState.Error).message,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Un Composable que muestra una lista de películas en una cuadrícula vertical.
 *
 * @param movies La lista de [Movie] a mostrar.
 * @param onMovieClick Devolución de llamada para cuando se hace clic en una película.
 */
@Composable
fun MovieGrid(movies: List<Movie>, onMovieClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp), // Las columnas se adaptan al tamaño disponible.
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieGridItem(movie = movie, onMovieClick = onMovieClick)
        }
    }
}

/**
 * Un Composable que representa un solo elemento en la cuadrícula de películas.
 *
 * @param movie La [Movie] a mostrar.
 * @param onMovieClick Devolución de llamada para cuando se hace clic en el elemento.
 * @param modifier El [Modifier] a aplicar a este Composable.
 */
@Composable
fun MovieGridItem(movie: Movie, onMovieClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onMovieClick(movie.id) }
    ) {
        Column {
            // Contenedor del póster de la película. El color de fondo es un marcador de posición.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Green) // TODO: Reemplazar con la imagen real de la película.
            )
            Column(modifier = Modifier.padding(12.dp)) {
                // Muestra el título de la película.
                Text(
                    text = movie.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Muestra el director de la película.
                Text(
                    text = movie.director,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}