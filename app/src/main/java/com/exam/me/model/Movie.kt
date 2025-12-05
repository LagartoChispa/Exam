package com.exam.me.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val director: String,
    val genre: String,
    val duration: Int,
    val synopsis: String,
    val releaseDate: String,
    val posterUrl: String,
    val trailerUrl: String
)