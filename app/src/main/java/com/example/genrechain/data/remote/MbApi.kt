package com.example.genrechain.data.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MbApi {
    private const val BASE_URL = "https://musicbrainz.org/ws/2/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(MbOkHttp.client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: MusicBrainzService = retrofit.create(MusicBrainzService::class.java)
}
