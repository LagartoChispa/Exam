package com.exam.me.ui.main

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.exam.me.model.UserProfile

/**
 * Pantalla de perfil de usuario. Muestra la información del usuario y permite la edición.
 *
 * @param viewModel El [ProfileViewModel] que gestiona el estado y la lógica de esta pantalla.
 * @param onNavigateBack Devolución de llamada para navegar a la pantalla anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    // Recopila los diversos estados del ViewModel.
    val profileState by viewModel.profileState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    // Lanzador para obtener una imagen de la cámara.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                viewModel.uploadProfileImage(bitmap)
            }
        }
    )

    // Lanzador para solicitar el permiso de la cámara.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
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
            // Muestra el contenido en función del estado del perfil.
            when (val state = profileState) {
                is ProfileState.Loading -> {
                    // Muestra un indicador de carga mientras se carga el perfil.
                    CircularProgressIndicator()
                }
                is ProfileState.Success -> {
                    // Muestra el contenido del perfil cuando la carga es exitosa.
                    ProfileContent(state.profile, formState, viewModel, onImageChangeClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    })
                }
                is ProfileState.Error -> {
                    // Muestra un mensaje de error si la carga del perfil falla.
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * El contenido principal de la pantalla de perfil.
 *
 * @param profile El perfil de usuario a mostrar.
 * @param formState El estado del formulario de edición.
 * @param viewModel El [ProfileViewModel] para gestionar las acciones del usuario.
 * @param onImageChangeClick Devolución de llamada para cuando se solicita un cambio de imagen.
 */
@Composable
fun ProfileContent(
    profile: UserProfile,
    formState: ProfileFormState,
    viewModel: ProfileViewModel,
    onImageChangeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenedor para la imagen de perfil y el botón de cambio de imagen.
        Box(contentAlignment = Alignment.BottomEnd) {
            SubcomposeAsyncImage(
                model = profile.profileImageUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    Image(imageVector = Icons.Default.Person, contentDescription = "Foto de perfil por defecto")
                }
            )
            IconButton(onClick = onImageChangeClick, modifier = Modifier.offset(x = 10.dp, y = 10.dp)) {
                Icon(Icons.Filled.AddAPhoto, "Cambiar foto", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Campo de texto para el nombre del usuario.
        OutlinedTextField(
            value = formState.nombre,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.nombreError != null,
            trailingIcon = { if (formState.nombreError != null) Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error) },
            supportingText = { if (formState.nombreError != null) Text(text = formState.nombreError!!, color = MaterialTheme.colorScheme.error) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para el email del usuario (solo lectura).
        OutlinedTextField(
            value = formState.email,
            onValueChange = { /* El email no es editable */ },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false // El email es de solo lectura
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para guardar los cambios en el perfil.
        Button(
            onClick = { viewModel.updateProfile() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }
    }
}