package com.exam.me.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("_id")
    val id: String,
    val titulo: String,
    val director: String,
    val anio: Int,
    val duracion: Int,
    val genero: String,
    val imagen: String?,
    val imagenThumbnail: String?,
    var posterUrl: String? = null // Optional field for external API data
)
