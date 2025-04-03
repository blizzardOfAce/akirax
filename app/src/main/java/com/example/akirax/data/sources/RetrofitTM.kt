package com.example.akirax.data.sources

import TicketmasterApiService
import com.example.akirax.data.api.CashfreeApiService
import com.example.akirax.data.api.CashfreeHeaderInterceptor
import com.example.akirax.data.api.TMDBApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val TICKETMASTER_BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
    private const val CASHFREE_BASE_URL = "https://sandbox.cashfree.com"

    val tmdbApi: TMDBApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApiService::class.java)
    }

    val ticketmasterApi: TicketmasterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TICKETMASTER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TicketmasterApiService::class.java)
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(CashfreeHeaderInterceptor())
        .build()

    val cashfreeApiService: CashfreeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(CASHFREE_BASE_URL)
            .client(okHttpClient) // Add the OkHttpClient with the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CashfreeApiService::class.java)
    }
}

