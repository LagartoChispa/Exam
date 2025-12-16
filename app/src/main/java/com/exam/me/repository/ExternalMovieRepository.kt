package com.exam.me.repository

import com.exam.me.network.ExternalApiService

class ExternalMovieRepository(private val externalApiService: ExternalApiService) {

    suspend fun findMoviePoster(movieTitle: String): String? {
        val searchResult = externalApiService.searchMovie(movieTitle = movieTitle)
        val bestMatch = searchResult.results.firstOrNull()
        return bestMatch?.posterPath
    }
}