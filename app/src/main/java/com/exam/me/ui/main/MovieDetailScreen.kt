package com.exam.me.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.exam.me.model.Movie
import com.exam.me.util.ProximitySensor

/**
 * Muestra los detalles de una película específica.
 *
 * Esta pantalla también utiliza el sensor de proximidad del dispositivo. Si un objeto
 * se acerca al dispositivo (activando el sensor), la pantalla se volverá negra.
 *
 * @param onNavigateBack Devolución de llamada para volver a la pantalla anterior.
 * @param movieDetailViewModel El ViewModel que proporciona los detalles de la película.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    onNavigateBack: () -> Unit,
    movieDetailViewModel: MovieDetailViewModel = viewModel()
) {
    // Recopila los diversos estados del ViewModel.
    val movieDetailState by movieDetailViewModel.movieDetailState.collectAsState()
    val context = LocalContext.current
    // Inicializa el sensor de proximidad.
    val proximitySensor = remember { ProximitySensor(context) }
    val isNear by proximitySensor.isNear.collectAsState()

    // Efecto para iniciar y detener la escucha del sensor de proximidad cuando el Composable entra o sale de la composición.
    DisposableEffect(Unit) {
        proximitySensor.startListening()
        onDispose {
            proximitySensor.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la película") },
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
                .padding(paddingValues)
        ) {
            // Muestra el contenido en función del estado de carga de los detalles de la película.
            when (val state = movieDetailState) {
                is MovieDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MovieDetailState.Success -> {
                    MovieDetailContent(movie = state.movie)
                }
                is MovieDetailState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            // Muestra una superposición negra con una animación de fundido cuando el sensor de proximidad detecta un objeto cercano.
            AnimatedVisibility(
                visible = isNear,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }
    }
}

/**
 * Muestra el contenido principal de los detalles de la película.
 *
 * @param movie La [Movie] cuyos detalles se mostrarán.
 */
@Composable
fun MovieDetailContent(movie: Movie) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Muestra la imagen del póster de la película si está disponible.
        if (movie.posterUrl != null) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            // Muestra un marcador de posición si la imagen del póster no está disponible.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Green) // Marcador de posición
            )
        }
        // Muestra los detalles textuales de la película.
        Column(modifier = Modifier.padding(16.dp)) {
            Text(movie.titulo, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Director: ${movie.director}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Género: ${movie.genero} | Duración: ${movie.duracion} min", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Año de lanzamiento: ${movie.anio}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}