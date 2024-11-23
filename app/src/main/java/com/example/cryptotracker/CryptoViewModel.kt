package com.example.cryptotracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class CryptoViewModel : ViewModel() {

    private val cryptoRepository = CryptoRepository()

    // Backing LiveData for crypto prices
    private val _cryptoList = MutableLiveData<List<CryptoCurrency>>()
    val cryptoList: LiveData<List<CryptoCurrency>> get() = _cryptoList

    // Manage favorites
    private val _favorites = MutableLiveData<Set<String>>()
    val favorites: LiveData<Set<String>> get() = _favorites

    // LiveData for market trend (Bullish/Bearish)
    private val _marketTrend = MutableLiveData<String>()
    val marketTrend: LiveData<String> get() = _marketTrend

    init {
        // Initial fetch of crypto prices
        fetchCryptoPrices()
    }

    // Trigger fetching crypto prices from the repository
    fun fetchCryptoPrices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prices = cryptoRepository.getCryptoPrices() // Assuming this is a suspend function

                // Sort the prices in descending order
                val sortedPrices = prices.sortedByDescending { it.current_price }

                // Check if the list has changed
                if (_cryptoList.value != sortedPrices) {
                    _cryptoList.postValue(sortedPrices) // Post the updated list
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle exceptions (e.g., log or notify user)
            }
        }
    }


    // Add cryptocurrency to favorites
    fun addFavorite(cryptoId: String) {
        val currentFavorites = _favorites.value.orEmpty().toMutableSet()
        currentFavorites.add(cryptoId)
        _favorites.postValue(currentFavorites)
    }

    // Remove cryptocurrency from favorites
    fun removeFavorite(cryptoId: String) {
        val currentFavorites = _favorites.value.orEmpty().toMutableSet()
        currentFavorites.remove(cryptoId)
        _favorites.postValue(currentFavorites)
    }

    // Calculate and update the market trend (Bullish/Bearish)
    private fun calculateMarketTrend(prices: List<CryptoCurrency>) {
        if (prices.isEmpty()) return

        // Calculate the average price change percentage over the last 24 hours
        val totalChange = prices.sumOf { it.priceChangePercentage24h }
        val averageChange = totalChange / prices.size

        // Determine market state
        val marketState = if (averageChange > 0) "Bullish" else "Bearish"
        _marketTrend.postValue(marketState)
    }

    fun updateCryptoList(newList: List<CryptoCurrency>) {
        _cryptoList.value = newList
    }

}

