package com.example.genrechain.data.repository

import android.util.Log
import com.example.genrechain.data.local.SelectedArtistStore
import com.example.genrechain.data.remote.MbApi
import com.example.genrechain.data.remote.MusicBrainzService
import com.example.genrechain.data.remote.dto.ArtistDetailResponse
import com.example.genrechain.data.remote.dto.ArtistDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class MusicBrainzRepository(
    private val api: MusicBrainzService = MbApi.service,
    private val store: SelectedArtistStore
) {
    private val gson = Gson()

    /**
     * A lightweight search that only returns the top‐level artist list
     * (genres may be null here if the API didn’t include them).
     */
    suspend fun searchArtist(name: String): List<ArtistDto> {
        val response = api.searchArtists(query = "artist:$name")
        Log.d("MB Repo", "raw artists list = ${gson.toJson(response.artists)}")
        return response.artists.map { apiArtist ->
            ArtistDto(
                id           = apiArtist.id,
                name         = apiArtist.name,
                disambiguation = apiArtist.disambiguation,
                // genres list here comes from the search endpoint; might be empty
                genres       = apiArtist.genres.orEmpty()
            )
        }
    }

    /**
     * Fetch full details for a single artist (including the genres array)
     * and map them into your unified ArtistDto.
     */
    suspend fun lookupArtistWithGenres(mbid: String): ArtistDto {
        val resp: ArtistDetailResponse = api.getArtist(mbid)
        return ArtistDto(
            id             = resp.id,
            name           = resp.name,
            disambiguation = resp.disambiguation,
            // pull out just the genre names
            genres         = resp.genres.map { it.name }
        )
    }

    /** Persist the user’s selected artist as JSON */
    suspend fun saveSelectedArtist(artist: ArtistDto) {
        store.save(artist)
    }

    /** Observe the persisted artist; starts up-to-date on disk */
    fun savedArtistFlow(): Flow<ArtistDto?> = store.flow
}
