package com.example.genrechain.data.repository

import com.example.genrechain.data.remote.MbApi
import com.example.genrechain.data.remote.dto.ArtistDto

class MusicBrainzRepository(
    private val api: com.example.genrechain.data.remote.MusicBrainzService = MbApi.service
) {
    suspend fun searchArtist(name: String): List<ArtistDto> =
        api.searchArtists(query = "artist:$name").artists
}
