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
    private val api: MusicBrainzService,
    private val store: SelectedArtistStore
) {
    private val gson = Gson()

    // returns top level list of matching artists
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

    // returns artist detail with genres
    suspend fun lookupArtistWithGenres(mbid: String): ArtistDto {
        val detail = api.getArtist(mbid)
        return ArtistDto(
            id            = detail.id,
            name          = detail.name,
            disambiguation= detail.disambiguation.orEmpty(),
            genres        = detail.genres.map { it.name },
            imageUrl      = null // or pull it from detail if MusicBrainz provides one
        )
    }

    // persist start artist
    suspend fun saveStartArtist(artist: ArtistDto) {store.saveStart(artist) }
    // persist target artist
    suspend fun saveTargetArtist(artist: ArtistDto) {store.saveTarget(artist)}

    fun savedStartArtistFlow(): Flow<ArtistDto?> = store.startFlow
    fun savedTargetArtistFlow(): Flow<ArtistDto?> = store.targetFlow
    /** Observe the persisted artist; starts up-to-date on disk */
//    fun savedArtistFlow(): Flow<ArtistDto?> = store.flow
}
