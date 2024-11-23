package com.example.cryptotracker

data class CryptoCurrency(
    val id: String,
    val name: String,
    val symbol: String,
    val current_price: Double,
    val priceChangePercentage24h: Double
)