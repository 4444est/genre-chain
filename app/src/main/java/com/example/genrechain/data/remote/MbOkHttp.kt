package com.example.genrechain.data.remote

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object MbOkHttp {
    private const val USER_AGENT =
        "GenreChain/1.0 ( your.email@domain.com )"

    // 1. Add a logging interceptor to dump request & response bodies
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Build your OkHttpClient with both interceptors
    val client: OkHttpClient = OkHttpClient.Builder()
        // MusicBrainz requires a custom User-Agent header
        .addInterceptor { chain: Interceptor.Chain ->
            val newReq = chain.request().newBuilder()
                .header("User-Agent", USER_AGENT)
                .build()
            chain.proceed(newReq)
        }
        // Log everything (URL, headers, body) for debugging
        .addInterceptor(loggingInterceptor)
        // (optional) bump timeouts if you need them
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}
