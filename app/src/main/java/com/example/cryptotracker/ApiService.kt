package com.example.cryptotracker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("coins/markets")
    fun getCryptoCurrencies(
        @Query("vs_currency") vsCurrency: String = "usd"
    ): Call<List<CryptoCurrency>>
}


