package com.example.genrechain.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: String,
    val name: String,
    val disambiguation: String?,
    val genres: List<String> = emptyList()
)
