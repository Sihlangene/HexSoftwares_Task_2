package com.example.cryptotracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cryptoViewModel: CryptoViewModel
    private lateinit var adapter: CryptoAdapter
    private lateinit var marketTrendTextView: TextView
    private lateinit var sortButton: Button // New button to toggle sorting
    private var isSortedDescending = false // Flag to track sorting order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        cryptoViewModel = ViewModelProvider(this)[CryptoViewModel::class.java]

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter and set it to RecyclerView
        adapter = CryptoAdapter(this, emptyList(), emptySet()) { cryptoId ->
            val favorites = cryptoViewModel.favorites.value.orEmpty()
            if (favorites.contains(cryptoId)) {
                cryptoViewModel.removeFavorite(cryptoId)
            } else {
                cryptoViewModel.addFavorite(cryptoId)
            }
        }
        recyclerView.adapter = adapter

        // Initialize TextView to display market trend
        marketTrendTextView = findViewById(R.id.marketTrendTextView)

        // Initialize sort button
        sortButton = findViewById(R.id.sortButton)
        sortButton.setOnClickListener {
            toggleSorting() // Handle sorting when the button is clicked
        }

        // Observe the list of cryptocurrencies and update the adapter
        cryptoViewModel.cryptoList.observe(this) { cryptoList ->
            val sortedList = if (isSortedDescending) {
                cryptoList.sortedByDescending { it.current_price }
            } else {
                cryptoList.sortedBy { it.current_price }
            }
            val favorites = cryptoViewModel.favorites.value.orEmpty()
            adapter.updateData(sortedList, favorites)
            updateMarketTrend(cryptoList)  // Update market trend based on prices
        }

        // Observe the market trend and update the TextView
        cryptoViewModel.marketTrend.observe(this) { marketState ->
            marketTrendTextView.text = "Market is $marketState"
            marketTrendTextView.setTextColor(
                if (marketState == "Bullish") getColor(R.color.green)
                else getColor(R.color.red)
            )
        }

        // Start periodic updates using coroutines
        startPeriodicUpdates()
    }

    // Periodically fetch new cryptocurrency prices every 2 minutes
    private fun startPeriodicUpdates() {
        lifecycleScope.launch {
            while (true) {
                cryptoViewModel.fetchCryptoPrices()
                delay(2 * 60 * 1000L)
            }
        }
    }

    // Toggle sorting order and refresh the list
    private fun toggleSorting() {
        isSortedDescending = !isSortedDescending
        sortButton.text = if (isSortedDescending) "Sort Ascending" else "Sort Descending"

        // Refresh the list with the new sorting order
        val currentList = cryptoViewModel.cryptoList.value.orEmpty()
        val sortedList = if (isSortedDescending) {
            currentList.sortedByDescending { it.current_price }
        } else {
            currentList.sortedBy { it.current_price }
        }
        val favorites = cryptoViewModel.favorites.value.orEmpty()
        adapter.updateData(sortedList, favorites)
    }

    // Calculate and update the market trend (Bullish or Bearish)
    private fun updateMarketTrend(cryptoList: List<CryptoCurrency>) {
        val totalChange = cryptoList.sumOf { it.priceChangePercentage24h }
        val averageChange = totalChange / cryptoList.size

        val marketTrend = if (averageChange > 0) "Bullish" else "Bearish"
        marketTrendTextView.text = "Market is $marketTrend"
        marketTrendTextView.setTextColor(
            if (marketTrend == "Bullish") getColor(R.color.green) else getColor(R.color.red)
        )
    }
}





