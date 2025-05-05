package com.example.genrechain.data.remote

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object MbOkHttp {
    private const val USER_AGENT =
        "GenreChain/1.0 ( your.email@domain.com )"

    // add logging interceptor to see all network traffic
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // build OkHttpClient with custom User-Agent and logging interceptor
    val client: OkHttpClient = OkHttpClient.Builder()
        // MusicBrainz requires a custom User-Agent header
        .addInterceptor { chain: Interceptor.Chain ->
            val newReq = chain.request().newBuilder()
                .header("User-Agent", USER_AGENT)
                .build()
            chain.proceed(newReq)
        }
        // log everything (URL, headers, body) for debugging
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}
