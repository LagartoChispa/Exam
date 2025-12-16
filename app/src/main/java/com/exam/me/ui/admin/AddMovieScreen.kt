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

/**
 * Una pantalla Composable para que los administradores agreguen nuevas películas a la base de datos.
 *
 * @param viewModel El [AddMovieViewModel] que gestiona el estado y la lógica de esta pantalla.
 * @param onNavigateBack Una función de devolución de llamada para navegar a la pantalla anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    viewModel: AddMovieViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    // Recopila los diversos estados del ViewModel.
    val formState by viewModel.formState.collectAsState()
    val addMovieResult by viewModel.addMovieResult.collectAsState()

    // Cuando la película se agrega con éxito, navega hacia atrás.
    LaunchedEffect(addMovieResult) {
        if (addMovieResult is AddMovieResult.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar nueva película") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
            // Campos del formulario para los detalles de la película.
            OutlinedTextField(value = formState.titulo, onValueChange = { viewModel.onFormChange(it, formState.director, formState.anio, formState.duracion, formState.genero) }, label = { Text("Título") }, isError = formState.tituloError != null, supportingText = { formState.tituloError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = formState.director, onValueChange = { viewModel.onFormChange(formState.titulo, it, formState.anio, formState.duracion, formState.genero) }, label = { Text("Director") }, isError = formState.directorError != null, supportingText = { formState.directorError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = formState.genero, onValueChange = { viewModel.onFormChange(formState.titulo, formState.director, formState.anio, formState.duracion, it) }, label = { Text("Género") }, isError = formState.generoError != null, supportingText = { formState.generoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = formState.anio, onValueChange = { viewModel.onFormChange(formState.titulo, formState.director, it, formState.duracion, formState.genero) }, label = { Text("Año") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = formState.anioError != null, supportingText = { formState.anioError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(value = formState.duracion, onValueChange = { viewModel.onFormChange(formState.titulo, formState.director, formState.anio, it, formState.genero) }, label = { Text("Duración (min)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = formState.duracionError != null, supportingText = { formState.duracionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón para enviar el formulario y crear la película.
            Button(onClick = { viewModel.createMovie() }, enabled = formState.isFormValid && addMovieResult !is AddMovieResult.Loading, modifier = Modifier.fillMaxWidth()) {
                Text("Crear película")
            }

            // Muestra un indicador de carga mientras se crea la película.
            AnimatedVisibility(visible = addMovieResult is AddMovieResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            // Muestra un mensaje de error si falla la creación de la película.
            AnimatedVisibility(visible = addMovieResult is AddMovieResult.Error) {
                val error = (addMovieResult as? AddMovieResult.Error)?.message ?: "Ocurrió un error desconocido"
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}