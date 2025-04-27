package com.example.genrechain.data.remote

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response

object MbOkHttp {
    private const val USER_AGENT =
        "GenreChain/1.0 ( your.email@domain.com )"

    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val newReq = chain.request().newBuilder()
                .header("User-Agent", USER_AGENT)   // MusicBrainz requirement
                .build()
            chain.proceed(newReq)
        }
        .build()
}
