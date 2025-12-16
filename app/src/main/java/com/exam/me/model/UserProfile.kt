package com.exam.me.model

import com.google.gson.annotations.SerializedName

// This data class is for the populated 'user' field in the UserProfile response
data class PopulatedUser(
    val email: String,
    val nombre: String
)

// This data class now matches the 'UsuarioProfile' entity from the backend more accurately
data class UserProfile(
    @SerializedName("_id")
    val id: String,
    val user: PopulatedUser,
    val nombre: String,
    val telefono: String,
    val preferencias: List<String>,
    @SerializedName("avatar")
    val profileImageUrl: String? = null
)
