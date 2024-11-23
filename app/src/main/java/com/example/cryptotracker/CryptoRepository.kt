package com.example.cryptotracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CryptoRepository {
    private val apiService = ApiClient.apiService

    // Suspend function to fetch crypto prices
    suspend fun getCryptoPrices(): List<CryptoCurrency> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCryptoCurrencies().execute()
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList() // Return an empty list if the response fails
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Return an empty list in case of an exception
            }
        }
    }
}

