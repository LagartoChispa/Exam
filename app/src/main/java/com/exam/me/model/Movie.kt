package com.exam.me.model

import com.google.gson.annotations.SerializedName

// This data class now matches the 'Pelicula' entity from the backend.
data class Movie(
    @SerializedName("_id")
    val id: String,
    val titulo: String,
    val director: String,
    val anio: Int,
    val duracion: Int,
    val genero: String,
    // The backend guide shows 'imagen' and 'imagenThumbnail' can be null
    val imagen: String?,
    val imagenThumbnail: String?
)
