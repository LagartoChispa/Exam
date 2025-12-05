package com.exam.me.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String,
    val email: String,
    val role: String,
    val nombre: String,
    val telefono: String? // Marked as optional as it might not be in every response
)
