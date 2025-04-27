package com.example.genrechain.data.remote

import com.example.genrechain.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicBrainzService {

    @GET("artist")
    suspend fun searchArtists(
        @Query("query") query: String,           // "artist:radiohead"
        @Query("limit") limit: Int = 10,
        @Query("fmt") fmt: String = "json"
    ): ArtistSearchResponse

    @GET("artist/{mbid}")
    suspend fun getArtist(
        @Path("mbid") mbid: String,
        @Query("inc") inc: String = "tags+genres",
        @Query("fmt") fmt: String = "json"
    ): Map<String, Any> /* keep flexible for now */
}
