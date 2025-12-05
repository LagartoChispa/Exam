package com.exam.me.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exam.me.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    onNavigateBack: () -> Unit,
    movieDetailViewModel: MovieDetailViewModel = viewModel()
) {
    val movieDetailState by movieDetailViewModel.movieDetailState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
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
        }
    }
}

@Composable
fun MovieDetailContent(movie: Movie) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Green square placeholder for the movie poster, as requested
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Green)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(movie.titulo, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Director: ${movie.director}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Genre: ${movie.genero} | Duration: ${movie.duracion} min", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Release Year: ${movie.anio}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
