package com.example.genrechain.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDetailResponse(
    val id: String,
    val name: String,
    val disambiguation: String? = null,
    val genres: List<GenreEntry> = emptyList(),
    val tags:   List<TagEntry>   = emptyList()
)

@Serializable
data class GenreEntry(
    val id: String,
    val name: String,
    val count: Int,
    val disambiguation: String? = null
)

@Serializable
data class TagEntry(
    val name: String,
    val count: Int
)
