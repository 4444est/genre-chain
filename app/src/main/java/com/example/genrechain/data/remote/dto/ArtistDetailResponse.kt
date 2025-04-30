package com.example.genrechain.data.remote.dto

import com.squareup.moshi.Json

data class ArtistDetailResponse(
    val id: String,
    val name: String,
    val disambiguation: String? = null,
    val genres: List<GenreDto> = emptyList()
)

data class GenreDto(
    @Json(name = "name") val name: String
)
