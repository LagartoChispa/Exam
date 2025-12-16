package com.exam.me.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    viewModel: AddMovieViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val addMovieResult by viewModel.addMovieResult.collectAsState()

    LaunchedEffect(addMovieResult) {
        if (addMovieResult is AddMovieResult.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Movie") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Form Fields
            OutlinedTextField(value = formState.titulo, onValueChange = { viewModel.onFormChange(it, formState.director, formState.anio, formState.duracion, formState.genero) }, label = { Text("Title") }, isError = formState.tituloError != null, supportingText = { formState.tituloError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = formState.director, onValueChange = { viewModel.onFormChange(formState.titulo, it, formState.anio, formState.duracion, formState.genero) }, label = { Text("Director") }, isError = formState.directorError != null, supportingText = { formState.directorError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = formState.genero, onValueChange = { viewModel.onFormChange(formState.titulo, formState.director, formState.anio, formState.duracion, it) }, label = { Text("Genre") }, isError = formState.generoError != null, supportingText = { formState.generoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = formState.anio, onValueChange = { viewModel.onFormChange(formState.titulo, formState.director, it, formState.duracion, formState.genero) }, label = { Text("Year") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = formState.anioError != null, supportingText = { formState.anioError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(value = formState.duracion, onValueChange = { viewModel.onFormChange(formState.titulo, formState.director, formState.anio, it, formState.genero) }, label = { Text("Duration (min)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = formState.duracionError != null, supportingText = { formState.duracionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { viewModel.createMovie() }, enabled = formState.isFormValid && addMovieResult !is AddMovieResult.Loading, modifier = Modifier.fillMaxWidth()) {
                Text("Create Movie")
            }

            AnimatedVisibility(visible = addMovieResult is AddMovieResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            AnimatedVisibility(visible = addMovieResult is AddMovieResult.Error) {
                val error = (addMovieResult as? AddMovieResult.Error)?.message ?: "An unknown error occurred"
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}