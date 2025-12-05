package com.exam.me.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onMovieClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val filteredMovies by homeViewModel.filteredMovies.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val movieState by homeViewModel.movieState.collectAsState()
    val sessionManager = SessionManager(LocalContext.current)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CinePlus Catalogue") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            sessionManager.clearSession()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { homeViewModel.onSearchQueryChange(it) },
                label = { Text("Search by title or director...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            Box(modifier = Modifier.weight(1f)) {
                when (movieState) {
                    is MovieState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is MovieState.Success -> {
                        if (filteredMovies.isEmpty() && searchQuery.isNotEmpty()) {
                            Text("No results found", modifier = Modifier.align(Alignment.Center))
                        } else {
                            MovieGrid(movies = filteredMovies, onMovieClick = onMovieClick)
                        }
                    }
                    is MovieState.Error -> {
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

@Composable
fun MovieGrid(movies: List<Movie>, onMovieClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(movies) { movie ->
            MovieGridItem(movie = movie, onMovieClick = onMovieClick)
        }
    }
}

@Composable
fun MovieGridItem(movie: Movie, onMovieClick: (String) -> Unit) {
    Card(
        modifier = Modifier.clickable { onMovieClick(movie.id) }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Green)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = movie.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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