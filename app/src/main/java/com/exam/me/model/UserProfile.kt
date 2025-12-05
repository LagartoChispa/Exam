package com.exam.me.model

import com.google.gson.annotations.SerializedName

// This data class matches the 'UsuarioProfile' entity from the backend.
data class UserProfile(
    @SerializedName("_id")
    val id: String,
    val user: String, // This will be the ID of the user
    val nombre: String,
    val telefono: String,
    val preferencias: List<String>
)
