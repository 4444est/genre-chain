package com.example.genrechain.data.repository

import com.example.genrechain.data.remote.MbApi
import com.example.genrechain.data.remote.dto.ArtistDto

class MusicBrainzRepository(
    private val api: com.example.genrechain.data.remote.MusicBrainzService = MbApi.service
) {
    suspend fun searchArtist(name: String): List<ArtistDto> {
        val response = api.searchArtists(query = "artist:$name")
        return response.artists.map { apiArtist ->
            ArtistDto(
                id = apiArtist.id,
                name = apiArtist.name,
                disambiguation = apiArtist.disambiguation,
                genres = apiArtist.genres ?: emptyList()  // 🔥 fallback if genres are missing
            )
        }
    }
}
